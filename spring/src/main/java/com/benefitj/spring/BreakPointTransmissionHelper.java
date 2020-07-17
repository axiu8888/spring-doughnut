package com.benefitj.spring;

import com.google.common.net.HttpHeaders;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   * 支持断点续传的下载方式
   *
   * @param request          请求
   * @param response         响应
   * @param file             下载的文件
   * @param downloadFilename 下载的文件名
   * @return 返回下载的长度
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static BreakPointTransmissionHelper.RangeSettings download(HttpServletRequest request,
                                                                    HttpServletResponse response,
                                                                    final File file,
                                                                    final String downloadFilename) throws FileNotFoundException, IOException {
    return download(request, response, file, downloadFilename, true);
  }

  /**
   * 支持断点续传的下载方式
   *
   * @param request          请求
   * @param response         响应
   * @param file             下载的文件
   * @param downloadFilename 下载的文件名
   * @param acceptRanges     是否支持断点续传
   * @return 返回下载的长度
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static BreakPointTransmissionHelper.RangeSettings download(HttpServletRequest request,
                                                                    HttpServletResponse response,
                                                                    final File file,
                                                                    final String downloadFilename,
                                                                    final boolean acceptRanges) throws FileNotFoundException, IOException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getAbsolutePath());
    }
    long count = 0;
    final BreakPointTransmissionHelper.RangeSettings settings =
        BreakPointTransmissionHelper.setResponseHeaders(request, response, file, downloadFilename, acceptRanges);
    try (final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
      // 跳过 n 个字节
      if (settings.getStart() > 0) {
        bis.skip(settings.getStart());
      }

      final ServletOutputStream out = response.getOutputStream();

      final byte[] buff = new byte[1024 << 4];
      int len;
      while (count < settings.getContentLength()) {
        len = bis.read(buff, 0, (int) Math.min(buff.length, settings.getContentLength() - count));
        count += len;
        out.write(buff, 0, len);
      }
      out.flush();
    } catch (ClientAbortException e) {
      // 客户端被强制关闭，
      logger.error("throws: {}", e.getMessage());
    } finally {
      settings.setDownloadLength(count);
    }
    return settings;
  }

  /**
   * 设置响应
   *
   * @param request          请求
   * @param response         响应
   * @param file             文件
   * @param downloadFilename 下载的文件名
   * @param acceptRanges     是否支持断线续传
   * @return
   */
  public static RangeSettings setResponseHeaders(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 File file,
                                                 String downloadFilename,
                                                 boolean acceptRanges) {
    RangeSettings settings = parseRangeHeader(request, file, acceptRanges);
    setResponseHeaders(response, settings, downloadFilename);
    return settings;
  }

  /**
   * Range
   */
  public static RangeSettings parseRangeHeader(HttpServletRequest request, File downloadFile, boolean acceptRanges) {
    String rangeHeader = request.getHeader("Range");
    RangeSettings settings;
    if ((rangeHeader == null || "".equals(rangeHeader.trim())) || !acceptRanges) {
      settings = new RangeSettings(downloadFile);
    } else {
      settings = getSettings(downloadFile, rangeHeader.replaceFirst("bytes=", ""));
    }
    return settings;
  }

  public static void setResponseHeaders(HttpServletResponse response, RangeSettings settings, String fileName) {
    response.addHeader("Content-Disposition", "attachment; filename=" +
        new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));
    // set the MIME type.
    response.setContentType(getContentType(fileName));
    if (settings.isRange()) {
      // 支持断点续传
      response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
      response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(settings.getContentLength()));
      String contentRange = "bytes " + settings.getStart() + "-" + settings.getEnd() + "/" + settings.getTotalLength();
      response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
      response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
    } else {
      response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(settings.getTotalLength()));
    }
  }

  private static RangeSettings getSettings(File downloadFile, String range) {
    long start, end, contentLength, totalLength = downloadFile.length();
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
    return new RangeSettings(downloadFile, start, end, contentLength, totalLength, true);
  }


  public static String getContentType(String returnFileName) {
    String contentType = "application/octet-stream";
    if (returnFileName.lastIndexOf(".") < 0)
      return contentType;
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


  public static class RangeSettings {

    private final File downloadFile;
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

    public RangeSettings(File downloadFile) {
      this(downloadFile, 0, downloadFile.length() - 1, downloadFile.length(), downloadFile.length(), false);
    }

    public RangeSettings(File downloadFile, long start, long end, long contentLength, long totalLength, boolean range) {
      this.downloadFile = downloadFile;
      this.start = start;
      this.end = end;
      this.contentLength = contentLength;
      this.totalLength = totalLength;
      this.range = range;
    }

    public File getDownloadFile() {
      return downloadFile;
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

}
