package com.benefitj.spring.influxdb.spring;


import com.benefitj.spring.influxdb.template.InfluxTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * InfluxDB初始化
 */
@Slf4j
public class InfluxInitializingBean implements InitializingBean {

  private InfluxTemplate template;

  public InfluxInitializingBean(InfluxTemplate template) {
    this.template = template;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      template.createDatabase(template.getDatabase());
    } catch (Exception e) {
      log.error("throw: " + e.getMessage(), e);
    }
  }
}
