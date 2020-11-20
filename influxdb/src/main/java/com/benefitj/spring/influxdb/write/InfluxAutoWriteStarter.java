package com.benefitj.spring.influxdb.write;

import com.benefitj.spring.influxdb.template.InfluxDBTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 程序启动时自动上传InfluxDB的行协议文件的监听
 */
public class InfluxAutoWriteStarter implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger logger = LoggerFactory.getLogger(InfluxAutoWriteStarter.class);

  private InfluxDBTemplate template;
  private InfluxWriteProperty property;

  public InfluxAutoWriteStarter() {
  }

  public InfluxAutoWriteStarter(InfluxDBTemplate template, InfluxWriteProperty property) {
    this.template = template;
    this.property = property;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (getProperty().isAutoUpload()) {
      new Thread(() -> {
        InfluxWriteProperty property = getProperty();
        File cacheDir = new File(property.getCacheDir());
        long before = TimeUnit.MINUTES.toMillis(10);
        checkAndUploadFile(cacheDir, before, Collections.emptyList());
      }).start();
    }
  }

  /**
   * 检查并上传文件
   *
   * @param file       文件
   * @param before     某个时间之前的数据
   * @param ignoreDirs 忽略的文件或目录
   */
  protected void checkAndUploadFile(File file, long before, List<String> ignoreDirs) {
    if (file == null || !file.exists()) {
      return;
    }

    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        for (File tmpFile : files) {
          if (tmpFile.isDirectory()) {
            checkAndUploadFile(tmpFile, before, ignoreDirs);
          } else {
            if (tmpFile.lastModified() >= before) {
              uploadFile(tmpFile, ignoreDirs);
            }
          }
        }
      } else if (!isIgnoreDir(file, ignoreDirs)) {
        // 删除空目录
        file.delete();
      }
    } else {
      uploadFile(file, Collections.emptyList());
    }
  }

  /**
   * 检查并上传文件
   *
   * @param file       文件
   * @param ignoreDirs 忽略的文件或目录
   */
  protected void uploadFile(File file, List<String> ignoreDirs) {
    if (file.isFile() && file.length() > 0
        && file.getName().endsWith(getProperty().getSuffix())) {
      logger.info("上传文件, name: {}, path: {}, length: {}MB", file.getName(),
          file.getAbsolutePath(), String.format("%.2f", ((file.length() * 1.0f) / (1024 << 10))));
      getTemplate().write(file);
      file.delete();

      File dir = file.getParentFile();
      if (dir.length() == 0 && !isIgnoreDir(dir, ignoreDirs)) {
        dir.delete();
      }
    }
  }

  /**
   * 是忽略文件
   *
   * @param dir        文件
   * @param ignoreDirs 忽略的文件
   * @return 返回是否为忽略
   */
  public boolean isIgnoreDir(File dir, List<String> ignoreDirs) {
    return ignoreDirs.stream().anyMatch(ignoreDir -> dir.getName().equals(ignoreDir));
  }

  public InfluxDBTemplate getTemplate() {
    return template;
  }

  public void setTemplate(InfluxDBTemplate template) {
    this.template = template;
  }

  public InfluxWriteProperty getProperty() {
    return property;
  }

  public void setProperty(InfluxWriteProperty property) {
    this.property = property;
  }

}
