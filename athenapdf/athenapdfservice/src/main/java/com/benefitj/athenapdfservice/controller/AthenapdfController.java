package com.benefitj.athenapdfservice.controller;

import com.benefitj.core.EventLoop;
import com.benefitj.core.HexUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.concurrent.CanceableScheduledFuture;
import com.benefitj.spring.BreakPointTransmissionHelper;
import com.benefitj.spring.aop.web.AopWebPointCut;
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
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
   * @param request    HTTP请求
   * @param response   HTTP响应
   * @param url        HTML的路径
   * @param filename   文件名，可选
   * @param encodeType 编码格式
   * @param force      是否强制生成
   */
  @GetMapping("/create")
  public void create(HttpServletRequest request,
                     HttpServletResponse response,
                     String url,
                     String filename,
                     String encodeType,
                     Boolean force) throws IOException {
    url = url != null ? url.trim() : "";
    long start = System.currentTimeMillis();
    if (StringUtils.isNoneBlank(encodeType) && !url.startsWith("http")) {
      if ("base64".equalsIgnoreCase(encodeType)) {
        url = new String(Base64.getDecoder().decode(url));
      } else if ("hex".equalsIgnoreCase(encodeType)) {
        url = new String(HexUtils.hexToBytes(url));
      }
    }
    try {
      if (StringUtils.isBlank(url)) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().write("缺少url参数");
        return;
      }
      URL ignore = new URL(url);

      if (!athenapdfHelper.supportDocker()) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().write("不支持docker环境!");
      }
    } catch (MalformedURLException e) {
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.getWriter().write("错误的url参数");
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
        log.info("生成PDF失败, filename: {}, destFile: {}, url: {}, \ncmd: {}, \nmsg: {}, error: {}"
            , filename, pdf.getAbsolutePath(), url, call.getCmd(), call.getMessage(), call.getError());
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
    ScheduledFuture<?> original = EventLoop.single().schedule(() -> {
      IOUtils.deleteFile(pdf);
      deleteTimers.remove(url);
    }, delay, TimeUnit.SECONDS);
    DeleteTimer<?> timer = new DeleteTimer<>(original);
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


  static class DeleteTimer<V> extends CanceableScheduledFuture<V> {

    public DeleteTimer(ScheduledFuture<V> original) {
      super(original);
    }

    public String getUrl() {
      return getAttribute("url");
    }

    public void setUrl(String url) {
      this.setAttribute("url", url);
    }

    public File getPdf() {
      return getAttribute("pdf");
    }

    public void setPdf(File pdf) {
      this.setAttribute("pdf", pdf);
    }
  }

}
