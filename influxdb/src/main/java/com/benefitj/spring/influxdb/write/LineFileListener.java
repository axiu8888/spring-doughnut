package com.benefitj.spring.influxdb.write;

import com.benefitj.core.Utils;
import com.benefitj.core.file.slicer.FileListener;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

public interface LineFileListener extends FileListener<LineFileWriter> {

  @Override
  void onHandle(LineFileWriter lineFileWriter, File file);


  static LineFileListener newLineFileListener(InfluxTemplate template) {
    return new LineFileListenerImpl(template);
  }


  /**
   * 上传到InfluxDB
   */
  @Slf4j
  class LineFileListenerImpl implements LineFileListener {

    InfluxTemplate template;

    public LineFileListenerImpl(InfluxTemplate template) {
      this.template = template;
    }

    @Override
    public void onHandle(LineFileWriter lineFileWriter, File file) {
      if (file.length() > 0) {
        for (int i = 0; i < 5; i++) {
          try {
            getTemplate().write(file);
            file.delete();
            log.debug("上传行协议文件, {}, {}MB", file.getAbsolutePath(), Utils.ofMB(file.length(), 4));
            return;
          } catch (Exception e) {
            log.warn("上传行协议文件出错, {}, {}, {}MB", e.getMessage(), file.getAbsolutePath(), Utils.ofMB(file.length(), 4));
          }
        }
      } else {
        file.delete();
      }
    }

    public InfluxTemplate getTemplate() {
      return template;
    }

    public void setTemplate(InfluxTemplate template) {
      this.template = template;
    }
  }

}
