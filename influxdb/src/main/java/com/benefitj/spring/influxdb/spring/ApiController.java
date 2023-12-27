package com.benefitj.spring.influxdb.spring;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.file.CompressUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

  @Autowired
  protected InfluxTemplate template;

  @ApiOperation("查询")
  @GetMapping("/query")
  public void download(HttpServletResponse response,
                       @ApiParam("数据库配置") @QueryBody Options options,
                       @ApiParam("查询语句") String sql,
                       @ApiParam("块大小") Integer chunkSize) {
    try {
      InfluxTemplate template = create(options);
      template.query(sql, chunkSize != null ? chunkSize : 1000)
          .subscribe(queryResult -> response.getWriter().write(JSON.toJSONString(queryResult))
              , e -> ServletUtils.write(response, 400, e.getMessage()));
    } catch (Exception e) {
      ServletUtils.write(response, 400, e.getMessage());
    }
  }

  @ApiOperation("导出")
  @GetMapping("/export")
  public void export(HttpServletRequest request,
                     HttpServletResponse response,
                     @ApiParam("数据库配置") @QueryBody Options options,
                     @ApiParam("匹配的表，逗号分割，为空表示全部匹配") String measurements,
                     @ApiParam("查询条件") String condition,
                     @ApiParam("文件名") String filename) {
    final InfluxTemplate template = create(options);
    File dir = new File("./tmp/" + IdUtils.uuid());
    try {
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
      if (IOUtils.length(dir, true) > 20) {
        File zip = CompressUtils.zip(dir);
        ServletUtils.download(request, response, zip, StringUtils.getIfBlank(filename, zip::getName));
      }
    } catch (Exception e) {
      ServletUtils.write(response, 400, e.getMessage());
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

  @ApiModel("连接配置")
  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class Options {
    @ApiModelProperty("InfluxDB连接路径")
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
  }


  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class MeasurementInfo {
    String name;
    Map<String, FieldKey> fieldKeys;
  }

}
