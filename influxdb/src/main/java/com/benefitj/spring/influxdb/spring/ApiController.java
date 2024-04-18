package com.benefitj.spring.influxdb.spring;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.*;
import com.benefitj.core.file.CompressUtils;
import com.benefitj.core.file.FileCopy;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.influxdb.InfluxApiFactory;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.FieldKey;
import com.benefitj.spring.influxdb.dto.QueryResult;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.template.InfluxTemplateImpl;
import com.benefitj.spring.mvc.query.QueryBody;
import com.squareup.moshi.Moshi;
import io.swagger.annotations.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Api(tags = "InfluxDB接口")
@AopWebPointCut
@RestController
@RequestMapping("/influxdb")
public class ApiController {

  @Autowired
  protected InfluxOptions options;

  @Autowired
  protected InfluxApiFactory factory;

  @Autowired
  protected PointConverterFactory converterFactory;

  final Map<String, ScheduledFuture<?>> deleteTask = new ConcurrentHashMap<>();

  @ApiOperation("查询")
  @GetMapping("/query")
  public void download(HttpServletResponse response,
                       @ApiParam("数据库配置") @QueryBody Options options,
                       @ApiParam("查询语句") String sql,
                       @ApiParam("块大小") Integer chunkSize) {
    InfluxTemplate template = create(options);
    template.query(sql, chunkSize != null ? chunkSize : 1000)
        .subscribe(queryResult -> response.getWriter().write(JSON.toJSONString(queryResult))
            , e -> ServletUtils.write(response, 400, e.getMessage()));
  }

  @ApiOperation("导出")
  @GetMapping("/export")
  public void export(HttpServletRequest request,
                     HttpServletResponse response,
                     @ApiParam("数据库配置") @QueryBody Options options,
                     @ApiParam("匹配的表，逗号分割，为空表示全部匹配") String measurements,
                     @ApiParam("where之后的查询条件, 如: time >= now() - 1d AND time < now()") String condition,
                     @ApiParam("文件名") String filename) {
    final InfluxTemplate template = create(options);
    InfluxOptions.Api apiOpts = this.options.getApi();
    String md5 = CodecUtils.md5((StringUtils.getIfBlank(measurements, () -> "") + StringUtils.getIfBlank(condition, () -> "")).getBytes(StandardCharsets.UTF_8));
    File dir = new File(apiOpts.getCacheDir(), md5);
    try {
      if (!dir.exists()) {
        List<String> measurementList = StringUtils.isNotBlank(measurements)
            ? Arrays.asList(measurements.split(","))
            : Collections.emptyList();
        template.getMeasurements()
            .stream()
            .filter(name -> measurementList.isEmpty() || measurementList.contains(name))
            .map(name -> MeasurementInfo.builder()
                .name(name)
                .fieldKeys(template.getFieldKeyMap(name, true))
                .build())
            .forEach(measurementInfo -> {
              File line = IOUtils.createFile(dir, measurementInfo.name + ".line");
              template.export(line, measurementInfo.name, 5000, null, null, condition);
              if (line.length() <= 20) {
                line.delete(); // 没有数据，删除空文件
              }
            });
      } else {
        EventLoop.cancel(deleteTask.remove(dir.getName()));
      }
      if (IOUtils.length(dir, true) > 20) {
        File zip = CompressUtils.zip(dir);
        ServletUtils.download(request, response, zip, StringUtils.getIfBlank(filename, zip::getName));
        response.flushBuffer();
        FileCopy.cut(zip, new File(dir, zip.getName()));
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      deleteTask.put(dir.getName(), EventLoop.asyncIO(() -> IOUtils.delete(dir), apiOpts.getCacheDuration().getSeconds(), TimeUnit.SECONDS));
    }
  }

  @ApiOperation("导入")
  @PostMapping(value = "/load")
  public void load(HttpServletRequest request,
                   HttpServletResponse response,
                   @ApiParam("数据库配置") @QueryBody Options options) {
    MultipartFile[] files = options.getFiles();
    if (files == null || files.length == 0) {
      throw new IllegalStateException("缺少导入的文件");
    }
    final InfluxTemplate template = create(options);
    InfluxOptions.Api apiOpts = this.options.getApi();
    File dir = new File(apiOpts.getCacheDir(), "upload_" + IdUtils.uuid());
    try {
      Stream.of(files)
          .map(mf -> {
            File dest = IOUtils.createFile(dir, mf.getOriginalFilename());
            CatchUtils.tryThrow(() -> mf.transferTo(dest));
            return dest;
          })
          .map(ApiController::unzip)
          .forEach(template::write);
    } finally {
      IOUtils.delete(dir);
    }
  }

  InfluxTemplate create(Options op) {
    if (StringUtils.isAnyBlank(op.database, op.url)) {
      throw new IllegalStateException("缺少数据配置, db: " + op.database + ", url: " + op.url);
    }
    InfluxOptions copy = BeanHelper.copy(options);
    copy.setUrl(op.url);
    copy.setDatabase(op.database);
    copy.setUsername(op.username);
    copy.setPassword(op.password);
    copy.setRetentionPolicy(op.retentionPolicy);
    InfluxTemplateImpl newTemplate = new InfluxTemplateImpl();
    newTemplate.setConverterFactory(converterFactory);
    newTemplate.setOptions(copy);
    newTemplate.setApi(factory.create(copy));
    newTemplate.setJsonAdapter(new Moshi.Builder().build().adapter(QueryResult.class));
    return newTemplate;
  }

  static File unzip(File f) {
    String rawName = f.getName();
    String lrn = rawName.toLowerCase();
    if (lrn.endsWith(".gzip") || lrn.endsWith(".gz")) {
      int index = lrn.lastIndexOf(".gzip");
      index = index >= 0 ? index : lrn.lastIndexOf(".gz");
      String destName = rawName.substring(0, index);
      return CompressUtils.ungzip(f, new File(f.getParentFile(), destName));
    }
    if (lrn.endsWith(".zip")) {
      String destName = lrn.substring(0, lrn.length() - 4);
      return CompressUtils.unzip(f, new File(f.getParentFile(), destName));
    }
    return f;
  }

  @ApiModel("连接配置")
  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class Options {
    @ApiModelProperty("InfluxDB连接路径，如: http://192.168.1.123:8086")
    String url;
    @ApiModelProperty("用户名")
    String username;
    @ApiModelProperty("密码")
    String password;
    @ApiModelProperty("数据库")
    String database;
    @Builder.Default
    @ApiModelProperty("存储策略，默认 autogen")
    String retentionPolicy = "autogen";

    @ApiModelProperty("文件")
    MultipartFile[] files;
  }


  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class MeasurementInfo {
    String name;
    Map<String, FieldKey> fieldKeys;
  }

}
