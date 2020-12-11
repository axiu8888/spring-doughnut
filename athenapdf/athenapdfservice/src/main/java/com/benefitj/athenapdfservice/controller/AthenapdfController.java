package com.benefitj.athenapdfservice.controller;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.spring.BreakPointTransmissionHelper;
import com.benefitj.spring.aop.AopWebPointCut;
import com.benefitj.spring.athenapdf.AthenapdfCall;
import com.benefitj.spring.athenapdf.AthenapdfHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.*;

/**
 * 生成报告的接口
 */
@Slf4j
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

  private final Map<String, DeleteTimer<?>> deleteTimers = new ConcurrentHashMap<>();

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
                     String filename,
                     Boolean force) throws IOException {
    url = url != null ? url.trim() : "";
    long start = System.currentTimeMillis();
    try {
      if (StringUtils.isBlank(url)) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getOutputStream().write("缺少url参数".getBytes(StandardCharsets.UTF_8));
        return;
      }
      URL ignore = new URL(url);

      if (!athenapdfHelper.supportDocker()) {
        throw new UnsupportedOperationException("不支持docker环境!");
      }
    } catch (MalformedURLException e) {
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.getOutputStream().write("错误的url参数".getBytes(StandardCharsets.UTF_8));
      return;
    }

    File pdf;
    // 取消删除的调度
    DeleteTimer<?> timer = get(url);
    if (timer != null) {
      cancelDeleteTimer(timer.getUrl(), timer.getPdf());
      pdf = timer.getPdf();
      if (Boolean.TRUE.equals(force)) {
        IOUtils.deleteFile(pdf);
      }
    } else {
      pdf = new File(cacheDir, IdUtils.uuid() + ".pdf");
    }

    filename = StringUtils.isNotBlank(filename)
        ? (filename.endsWith(".pdf") ? filename : (filename + ".pdf"))
        : pdf.getName();

    boolean callAthenaPdf = false;

    if (!pdf.exists()) {
      callAthenaPdf = true;
      AthenapdfCall call = athenapdfHelper.execute(IOUtils.mkDirs(cacheDir), url, pdf.getName(), null);
      if (!call.isSuccessful()) {
        log.info("生成PDF失败, filename: {}, destFile: {}, url: {}", filename, pdf.getAbsolutePath(), url);
        return;
      }
      pdf = call.getPdf();
    }
    try {
      BreakPointTransmissionHelper.download(request, response, pdf, filename);
    } finally {
      // 最终删除文件
      scheduleDeleteTimer(url, pdf);
    }
    log.info("{}, size: {}, callAthenaPdf: {}, 使用时长: {}"
        , pdf.getAbsolutePath()
        , pdf.length()
        , callAthenaPdf
        , (System.currentTimeMillis() - start)
    );
  }

  protected void scheduleDeleteTimer(final String url, File pdf) {
    if (!pdf.exists()) {
      return;
    }
    DeleteTimer<?> timer = DeleteTimer.wrap(
        EventLoop.single().schedule(() -> {
          IOUtils.deleteFile(pdf);
          deleteTimers.remove(url);
        }, delay, TimeUnit.SECONDS));
    timer.setUrl(url);
    timer.setPdf(pdf);
    deleteTimers.put(url, timer);
  }

  protected DeleteTimer<?> get(String url) {
    return deleteTimers.remove(url);
  }

  protected void cancelDeleteTimer(String url, File pdf) {
    DeleteTimer<?> timer = deleteTimers.remove(url);
    if (timer != null && timer.getPdf().equals(pdf)) {
      timer.cancel(true);
    }
  }


  static class DeleteTimer<V> implements ScheduledFuture<V> {

    public static <V> DeleteTimer<V> wrap(ScheduledFuture<V> timer) {
      return new DeleteTimer<>(timer);
    }

    private final ScheduledFuture<V> original;
    /**
     * URL
     */
    private String url;
    /**
     * PDF文件
     */
    private File pdf;

    public DeleteTimer(ScheduledFuture<V> original) {
      this.original = original;
    }

    @Override
    public long getDelay(TimeUnit unit) {
      return original.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
      return original.compareTo(o);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return original.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return original.isDone();
    }

    @Override
    public boolean isDone() {
      return original.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
      return original.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return original.get(timeout, unit);
    }

    public ScheduledFuture<V> getOriginal() {
      return original;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public File getPdf() {
      return pdf;
    }

    public void setPdf(File pdf) {
      this.pdf = pdf;
    }
  }

}
