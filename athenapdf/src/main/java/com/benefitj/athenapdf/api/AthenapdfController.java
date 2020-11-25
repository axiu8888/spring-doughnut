package com.benefitj.athenapdf.api;

import com.benefitj.athenapdf.AthenapdfCall;
import com.benefitj.athenapdf.AthenapdfHelper;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.spring.BreakPointTransmissionHelper;
import com.benefitj.spring.aop.AopWebPointCut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 生成报告的接口
 */
@Slf4j
@ConditionalOnClass(RequestMapping.class)
@AopWebPointCut
@RestController
@RequestMapping("/athenapdf")
public class AthenapdfController {

  /**
   * 缓存目录
   */
  @Value("#{ @environment['spring.athenapdf.cache-dir'] ?: '/tmp/athenapdf-pdf/' }")
  private String cacheDir;
  /**
   * 延迟删除的时间，默认60秒
   */
  @Value("#{ @environment['spring.athenapdf.delay'] ?: 60 }")
  private long delay;

  @Autowired
  private AthenapdfHelper athenapdfHelper;

  private final Map<String, ScheduledFuture<?>> deleteTimers = new ConcurrentHashMap<>();

  /**
   * 生成PDF
   *
   * @param response HTTP响应
   * @param url      HTML的路径
   * @param filename 文件名，可选
   */
  @GetMapping("/create")
  public void create(HttpServletRequest request,
                     HttpServletResponse response,
                     String url,
                     String filename) throws IOException {
    long start = System.currentTimeMillis();
    try {
      if (StringUtils.isBlank(url)) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getOutputStream().write("缺少url参数".getBytes(StandardCharsets.UTF_8));
        return;
      }
      URL ignore = new URL(url);
    } catch (MalformedURLException e) {
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.getOutputStream().write("错误的url参数".getBytes(StandardCharsets.UTF_8));
      return;
    }

    filename = StringUtils.isNotBlank(filename)
        ? (filename.endsWith(".pdf") ? filename : (filename + ".pdf"))
        : IdUtils.uuid() + ".pdf";
    File pdf = new File(cacheDir, filename);

    // 取消删除的调度
    cancelDeleteTimer(pdf);

    try {
      if (!pdf.exists()) {
        AthenapdfCall call = athenapdfHelper.execute(IOUtils.mkDirs(cacheDir), url, filename, null);
        if (!call.isSuccessful()) {
          log.info("生成PDF失败, filename: {}, url: {}", filename, url);
          return;
        }
        pdf = call.getPdf();
      }
      BreakPointTransmissionHelper.download(request, response, pdf, filename);
      scheduleDeleteTimer(pdf);
    } finally {
      // 最终删除文件
      scheduleDeleteTimer(pdf);
    }
    log.info("{}, 使用时长: {}", filename, (System.currentTimeMillis() - start));
  }

  private void scheduleDeleteTimer(final File deletePdf) {
    ScheduledFuture<?> deleteTimer = EventLoop.single().schedule(() -> {
      IOUtils.deleteFile(deletePdf);
      deleteTimers.remove(deletePdf.getAbsolutePath());
    }, delay, TimeUnit.SECONDS);
    deleteTimers.put(deletePdf.getAbsolutePath(), deleteTimer);
  }

  private void cancelDeleteTimer(File pdf) {
    ScheduledFuture<?> deleteTimer = deleteTimers.remove(pdf.getAbsolutePath());
    if (deleteTimer != null) {
      deleteTimer.cancel(true);
    }
  }

}
