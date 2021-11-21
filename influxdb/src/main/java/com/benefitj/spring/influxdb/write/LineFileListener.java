package com.benefitj.spring.influxdb.write;

import com.benefitj.core.Unit;
import com.benefitj.core.file.slicer.FileListener;
import com.benefitj.spring.influxdb.template.RxJavaInfluxDBTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public interface LineFileListener extends FileListener<LineFileWriter> {

  @Override
  void onHandle(LineFileWriter lineFileWriter, File file);


  static LineFileListener newLineFileListener(RxJavaInfluxDBTemplate template) {
    return new LineFileListenerImpl(template);
  }


  /**
   * 上传到InfluxDB
   */
  class LineFileListenerImpl implements LineFileListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RxJavaInfluxDBTemplate template;

    public LineFileListenerImpl(RxJavaInfluxDBTemplate template) {
      this.template = template;
    }

    @Override
    public void onHandle(LineFileWriter lineFileWriter, File file) {
      try {
        if (file.length() > 0) {
          try {
            getTemplate().write(file);
            log.info("上传行协议文件, {}, {}MB", file.getAbsolutePath(), Unit.ofMB(file.length(), 4));
          } catch (Exception e) {
            log.warn("上传行协议文件出错, {}, {}, {}MB", e.getMessage(), file.getAbsolutePath(), Unit.ofMB(file.length(), 4));
            //e.printStackTrace();
          }
        }
      } finally {
        file.delete();
      }
    }

    public RxJavaInfluxDBTemplate getTemplate() {
      return template;
    }

    public void setTemplate(RxJavaInfluxDBTemplate template) {
      this.template = template;
    }
  }

}
