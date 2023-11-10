package com.benefitj.spring.influxdb.spring;

import com.alibaba.fastjson2.JSON;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.influxdb.InfluxApiFactory;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.QueryResult;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.template.InfluxTemplateImpl;
import com.benefitj.spring.mvc.query.QueryBody;
import com.squareup.moshi.Moshi;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

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
                       @ApiParam("数据库配置") @QueryBody @RequestBody Options options,
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

  InfluxTemplate create(Options op) {
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
  public static class Options {
    @ApiModelProperty("InfluxDB连接路径")
    String url;
    @ApiModelProperty("用户名")
    String username;
    @ApiModelProperty("密码")
    String password;
    @ApiModelProperty("数据库")
    String database;
    @ApiModelProperty("存储策略，默认 autogen")
    String retentionPolicy = "autogen";
  }
}
