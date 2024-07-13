package com.benefitj.spring.influxdb.write;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.TimeUtils;
import com.benefitj.core.Utils;
import com.benefitj.core.log.ILogger;
import com.benefitj.spring.influxdb.InfluxDBLogger;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 程序启动时自动上传InfluxDB的行协议文件的监听
 */
public class AppStarterAutoWriter {

  final ILogger log = InfluxDBLogger.get();

  InfluxTemplate template;

  InfluxOptions options;

  public AppStarterAutoWriter() {
  }

  public AppStarterAutoWriter(InfluxTemplate template, InfluxOptions options) {
    this.template = template;
    this.options = options;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onAppStart() {
    InfluxOptions.Writer optWriter = getOptions().getWriter();
    final File cacheDir = new File(new File(optWriter.getCacheDir())
        .getAbsolutePath()
        .replace("\\", "/")
        .replace("./", "/")
        .replace("//", "/")
    );
    if (optWriter.isAutoUpload()) {
      long before = TimeUnit.MINUTES.toMillis(5);
      EventLoop.asyncIOFixedRate(() -> {
        if (cacheDir.getFreeSpace() <= 50 * IOUtils.MB) return; // 磁盘空间不足50MB，则不上传数据
        File[] lines = cacheDir.listFiles(f -> f.length() > 0 // 长度大于0
            && !f.getName().startsWith("backup_") // 过滤掉备份数据
            && !f.getName().startsWith("err_") // 过滤掉错误数据
            && f.getName().endsWith(optWriter.getSuffix()) // line文件
            && TimeUtils.diffNow(f.lastModified()) >= before // 最近更新的时间超过10分钟
        );
        if (lines != null) {
          for (File line : lines) {
            try {
              log.info("自动上传line文件, name: {}, length: {}", line.getName(), Utils.fmtMB(line.length(), "0.00MB"));
              getTemplate().write(line);
              line.delete();
            } catch (Exception e) {
              log.error("AutoUploadLineFile error!", e);
            }
          }
        }
      }, 1, 1, TimeUnit.MINUTES);
    }

    // 检测磁盘空间
    EventLoop.asyncIOFixedRate(() -> {
      if (cacheDir.getFreeSpace() <= IOUtils.ofGB(1)) {
        log.error("磁盘空间不足1GB: {}", Utils.fmtGB(cacheDir.getFreeSpace(), "0.0000GB"));
      }
    }, 1, 1, TimeUnit.MINUTES);

  }


  public InfluxTemplate getTemplate() {
    return template;
  }

  public void setTemplate(InfluxTemplate template) {
    this.template = template;
  }

  public InfluxOptions getOptions() {
    return options;
  }

  public void setOptions(InfluxOptions options) {
    this.options = options;
  }
}
