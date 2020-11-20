package com.benefitj.spring.influxdb.write;

import com.benefitj.spring.influxdb.file.FileWriterPair;
import com.benefitj.spring.influxdb.file.LineFileListener;
import com.benefitj.spring.influxdb.template.InfluxDBTemplate;

import java.io.File;

/**
 * 上传到InfluxDB
 */
public class InfluxLineFileListener<T extends InfluxDBTemplate> implements LineFileListener {

  private T template;

  public InfluxLineFileListener(T template) {
    this.template = template;
  }

  @Override
  public void onHandleLineFile(FileWriterPair pair, File file) {
    try {
      if (file.length() > 0) {
        getTemplate().write(file);
      }
    } finally {
      file.delete();
    }
  }

  public T getTemplate() {
    return template;
  }

  public void setTemplate(T template) {
    this.template = template;
  }
}
