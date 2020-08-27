package com.benefitj.spring;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 断点重传工具类
 */
public class BreakPointTransmissionHelper {

  private static final Logger logger = LoggerFactory.getLogger(BreakPointTransmissionHelper.class);

  /**
   * 支持断点续传的上传方式
   *
   * @param request 请求
   * @param source  上传的文件
   * @param target  保存的文件
   * @return 返回上传的长度
   * @throws IOException IO异常
   */
  public static RangeSettings upload(HttpServletRequest request,
                                     final MultipartFile source,
                                     final File target) throws IOException {
    if (source == null || source.getSize() <= 0) {
      return new RangeSettings(0, 0, 0, 0, false);
    }

    final RangeSettings settings = parseRangeHeader(request, source.getSize(), true);
    try (final RandomAccessFile out = new RandomAccessFile(target, "rw");
         final BufferedInputStream bis = new BufferedInputStream(source.getInputStream());) {
      out.seek(settings.getStart());
      transferTo(bis, settings, (buff, len) -> out.write(buff, 0, len));
    }
    return settings;
  }

  /**
   * 支持断点续传的下载方式
   *
   * @param request  请求
   * @param response 响应
   * @param source   下载的文件
   * @param filename 下载的文件名
   * @return 返回下载的长度
   * @throws FileNotFoundException 文件找不到
   * @throws IOException           IO异常
   */
  public static BreakPointTransmissionHelper.RangeSettings download(HttpServletRequest request,
                                                                    HttpServletResponse response,
                                                                    final File source,
                                                                    final String filename) throws FileNotFoundException, IOException {
    return download(request, response, source, filename, true);
  }

  /**
   * 支持断点续传的下载方式
   *
   * @param request      请求
   * @param response     响应
   * @param source       下载的文件
   * @param filename     下载的文件名
   * @param acceptRanges 是否支持断点续传
   * @return 返回下载的长度
   * @throws FileNotFoundException 文件找不到
   * @throws IOException           IO异常
   */
  public static BreakPointTransmissionHelper.RangeSettings download(HttpServletRequest request,
                                                                    HttpServletResponse response,
                                                                    final File source,
                                                                    final String filename,
                                                                    final boolean acceptRanges) throws FileNotFoundException, IOException {
    if (!source.exists()) {
      throw new FileNotFoundException(source.getAbsolutePath());
    }

    final BreakPointTransmissionHelper.RangeSettings settings = setResponseHeaders(request, response, source, filename, acceptRanges);
    try (final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));) {
      // 跳过 n 个字节
      bis.skip(settings.getStart());

      final ServletOutputStream out = response.getOutputStream();
      transferTo(bis, settings, (buff, len) -> out.write(buff, 0, len));
      out.flush();
    } catch (ClientAbortException e) {
      // 客户端被强制关闭，
      logger.error("throws: {}", e.getMessage());
    }
    return settings;
  }

  private static void transferTo(BufferedInputStream bis, RangeSettings settings, BiConsumer<byte[], Integer> consumer) throws IOException {
    long count = 0;
    try {
      final byte[] buff = new byte[1024 << 4];
      int len;
      while (count < settings.getContentLength()) {
        len = bis.read(buff, 0, (int) Math.min(buff.length, settings.getContentLength() - count));
        count += len;
        consumer.accept(buff, len);
      }
    } finally {
      settings.setDownloadLength(count);
    }
  }

  /**
   * 设置响应
   *
   * @param request      请求
   * @param response     响应
   * @param source       文件
   * @param filename     下载的文件名
   * @param acceptRanges 是否支持断线续传
   * @return
   */
  public static RangeSettings setResponseHeaders(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 File source,
                                                 String filename,
                                                 boolean acceptRanges) {
    RangeSettings settings = parseRangeHeader(request, source.length(), acceptRanges);
    setResponseHeaders(response, settings, filename);
    return settings;
  }

  /**
   * Range
   *
   * @param request      HttpServletRequest
   * @param totalLength  数据的总长度
   * @param acceptRanges 是否断点续传
   * @return 返回解析的参数
   */
  public static RangeSettings parseRangeHeader(HttpServletRequest request, long totalLength, boolean acceptRanges) {
    String rangeHeader = request.getHeader("Range");
    RangeSettings settings;
    if ((rangeHeader == null || "".equals(rangeHeader.trim())) || !acceptRanges) {
      settings = new RangeSettings(totalLength);
    } else {
      settings = getSettings(totalLength, rangeHeader.replaceFirst("bytes=", ""));
    }
    return settings;
  }

  public static void setResponseHeaders(HttpServletResponse response, RangeSettings settings, String filename) {
    response.addHeader("Content-Disposition", "attachment; filename=" +
        new String(filename.getBytes(), StandardCharsets.ISO_8859_1));
    // set the MIME type.
    response.setContentType(getContentType(filename));
    if (settings.isRange()) {
      // 支持断点续传
      response.setHeader("Accept-Ranges", "bytes");
      response.addHeader("Content-Length", String.valueOf(settings.getContentLength()));
      String contentRange = "bytes " + settings.getStart() + "-" + settings.getEnd() + "/" + settings.getTotalLength();
      response.setHeader("Content-Range", contentRange);
      response.setStatus(206);
    } else {
      response.addHeader("Content-Length", String.valueOf(settings.getTotalLength()));
    }
  }

  private static RangeSettings getSettings(long totalLength, String range) {
    long start, end, contentLength;
    if (range.startsWith("-")) {
      contentLength = Long.parseLong(range.substring(1));
      end = totalLength - 1;
      start = totalLength - contentLength;
    } else if (range.endsWith("-")) {
      start = Long.parseLong(range.replace("-", ""));
      end = totalLength - 1;
      contentLength = totalLength - start;
    } else {
      String[] splits = range.split("-");
      start = Long.parseLong(splits[0]);
      end = Long.parseLong(splits[1]);
      contentLength = end - start + 1;
    }
    return new RangeSettings(start, end, contentLength, totalLength, true);
  }


  public static class RangeSettings {

    /**
     * 开始的位置
     */
    private long start;
    /**
     * 结束的位置（包含）
     */
    private long end;
    /**
     * 数据长度
     */
    private long contentLength;
    /**
     * 数据总长度
     */
    private long totalLength;
    /**
     * 是否包含Range
     */
    private boolean range;
    /**
     * 实际下载长度
     */
    private long downloadLength;

    public RangeSettings(long length) {
      this(0, length - 1, length, length, false);
    }

    public RangeSettings(long start, long end, long contentLength, long totalLength, boolean range) {
      this.start = start;
      this.end = end;
      this.contentLength = contentLength;
      this.totalLength = totalLength;
      this.range = range;
    }

    public long getStart() {
      return start;
    }

    public void setStart(long start) {
      this.start = start;
    }

    public long getEnd() {
      return end;
    }

    public void setEnd(long end) {
      this.end = end;
    }

    public long getContentLength() {
      return contentLength;
    }

    public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
    }

    public long getTotalLength() {
      return totalLength;
    }

    public void setTotalLength(long totalLength) {
      this.totalLength = totalLength;
    }

    public boolean isRange() {
      return range;
    }

    public void setRange(boolean range) {
      this.range = range;
    }

    public long getDownloadLength() {
      return downloadLength;
    }

    public void setDownloadLength(long downloadLength) {
      this.downloadLength = downloadLength;
    }

    public boolean isSuccessful() {
      return getContentLength() == getDownloadLength();
    }
  }


  public static String getContentType(String returnFileName) {
    String contentType = "application/octet-stream";
    if (returnFileName.lastIndexOf(".") < 0) {
      return contentType;
    }
    returnFileName = returnFileName.toLowerCase();
    returnFileName = returnFileName.substring(returnFileName.lastIndexOf(".") + 1);
    switch (returnFileName) {
      case "html":
      case "htm":
      case "shtml":
        contentType = "text/html";
        break;
      case "css":
        contentType = "text/css";
        break;
      case "xml":
        contentType = "text/xml";
        break;
      case "gif":
        contentType = "image/gif";
        break;
      case "jpeg":
      case "jpg":
        contentType = "image/jpeg";
        break;
      case "js":
        contentType = "application/x-javascript";
        break;
      case "atom":
        contentType = "application/atom+xml";
        break;
      case "rss":
        contentType = "application/rss+xml";
        break;
      case "mml":
        contentType = "text/mathml";
        break;
      case "txt":
        contentType = "text/plain";
        break;
      case "jad":
        contentType = "text/vnd.sun.j2me.app-descriptor";
        break;
      case "wml":
        contentType = "text/vnd.wap.wml";
        break;
      case "htc":
        contentType = "text/x-component";
        break;
      case "png":
        contentType = "image/png";
        break;
      case "tif":
      case "tiff":
        contentType = "image/tiff";
        break;
      case "wbmp":
        contentType = "image/vnd.wap.wbmp";
        break;
      case "ico":
        contentType = "image/x-icon";
        break;
      case "jng":
        contentType = "image/x-jng";
        break;
      case "bmp":
        contentType = "image/x-ms-bmp";
        break;
      case "svg":
        contentType = "image/svg+xml";
        break;
      case "jar":
      case "var":
      case "ear":
        contentType = "application/java-archive";
        break;
      case "doc":
        contentType = "application/msword";
        break;
      case "pdf":
        contentType = "application/pdf";
        break;
      case "rtf":
        contentType = "application/rtf";
        break;
      case "xls":
        contentType = "application/vnd.ms-excel";
        break;
      case "ppt":
        contentType = "application/vnd.ms-powerpoint";
        break;
      case "7z":
        contentType = "application/x-7z-compressed";
        break;
      case "rar":
        contentType = "application/x-rar-compressed";
        break;
      case "swf":
        contentType = "application/x-shockwave-flash";
        break;
      case "rpm":
        contentType = "application/x-redhat-package-manager";
        break;
      case "der":
      case "pem":
      case "crt":
        contentType = "application/x-x509-ca-cert";
        break;
      case "xhtml":
        contentType = "application/xhtml+xml";
        break;
      case "zip":
        contentType = "application/zip";
        break;
      case "mid":
      case "midi":
      case "kar":
        contentType = "audio/midi";
        break;
      case "mp3":
        contentType = "audio/mpeg";
        break;
      case "ogg":
        contentType = "audio/ogg";
        break;
      case "m4a":
        contentType = "audio/x-m4a";
        break;
      case "ra":
        contentType = "audio/x-realaudio";
        break;
      case "3gpp":
      case "3gp":
        contentType = "video/3gpp";
        break;
      case "mp4":
        contentType = "video/mp4";
        break;
      case "mpeg":
      case "mpg":
        contentType = "video/mpeg";
        break;
      case "mov":
        contentType = "video/quicktime";
        break;
      case "flv":
        contentType = "video/x-flv";
        break;
      case "m4v":
        contentType = "video/x-m4v";
        break;
      case "mng":
        contentType = "video/x-mng";
        break;
      case "asx":
      case "asf":
        contentType = "video/x-ms-asf";
        break;
      case "wmv":
        contentType = "video/x-ms-wmv";
        break;
      case "avi":
        contentType = "video/x-msvideo";
        break;
      default:
        contentType = "application/octet-stream";
        break;
    }
    return contentType;
  }


  /**
   * consumer
   */
  @FunctionalInterface
  interface BiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u) throws IOException;
  }


}
