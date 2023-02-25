package com.benefitj.influxdb.spring;


import com.benefitj.influxdb.template.InfluxTemplate;
import org.springframework.beans.factory.InitializingBean;

/**
 * InfluxDB初始化
 */
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
      e.printStackTrace();
    }
  }
}
