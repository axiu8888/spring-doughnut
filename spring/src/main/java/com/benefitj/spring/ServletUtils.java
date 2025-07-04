package com.benefitj.spring;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.Utils;
import com.benefitj.core.http.ContentType;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servlet工具
 */
public class ServletUtils {

  private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);

  public static String APPLICATION_FROM_DATA = "application/form-data";
  public static String APPLICATION_JSON = "application/json";
  public static String APPLICATION_XML = "application/xml";
  public static String APPLICATION_FROM_URLENCODED = "application/x-www-form-urlencoded";
  public static String APPLICATION_OCTET_STREAM = "application/octet-stream";


  /**
   * 传输文件
   *
   * @param src  源文件
   * @param dest 目标文件
   * @return 返回目标文件
   */
  public static File transferTo(MultipartFile src, File dest) {
    try {
      IOUtils.write(src.getInputStream(), dest);
      return dest;
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  @Nullable
  public static ServletRequestAttributes getRequestAttributes() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
  }

  /**
   * 获取当前请求的属性缓存，如果不存在，抛出 IllegalStateException 异常
   */
  public static ServletRequestAttributes currentRequestAttributes() throws IllegalStateException {
    return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
  }

  /**
   * 获取HTTP请求
   */
  public static HttpServletRequest getRequest() {
    return currentRequestAttributes().getRequest();
  }

  /**
   * 获取HTTP响应
   */
  public static HttpServletResponse getResponse() {
    return currentRequestAttributes().getResponse();
  }

  /**
   * 获取请求属性
   *
   * @param attr 属性
   * @return 返回属性对象
   */
  public static <T> T getRequestAttribute(String attr) {
    return getRequestAttribute(getRequest(), attr);
  }

  /**
   * 获取请求属性
   *
   * @param request 请求
   * @param attr    属性
   * @return 返回属性对象
   */
  public static <T> T getRequestAttribute(HttpServletRequest request, String attr) {
    return (T) request.getAttribute(attr);
  }

  /**
   * 获取当前请求路径
   */
  public static String getPath() {
    HttpServletRequest request = getRequest();
    return request.getContextPath() + request.getRequestURI();
  }

  /**
   * 获取当前请求路径
   */
  public static String getFullPath() {
    HttpServletRequest request = getRequest();
    return request.getContextPath() + request.getRequestURI() + "?" + request.getQueryString();
  }

  /**
   * 获取当前请求的首部
   */
  public static String getHeader(String headerName) {
    return getRequest().getHeader(headerName);
  }

  /**
   * 获取当前请求的首部
   */
  public static Map<String, String> getHeaderMap() {
    return getHeaderMap(getRequest());
  }

  /**
   * 获取请求首部
   */
  public static Map<String, String> getHeaderMap(HttpServletRequest request) {
    return Utils.toMap(request.getHeaderNames(), request::getHeader);
  }

  /**
   * 解析 Basic token
   * 格式： Authorization: Basic base64encode(username:password)
   *
   * @param token token
   * @return 返回解析后的数据
   */
  public static String[] parseBasicToken(String token) {
    if (StringUtils.isNotBlank(token)) {
      if (token.startsWith("Basic ") || token.startsWith("basic ")) {
        token = token.substring("Basic ".length());
      }
      byte[] decode = Base64.getDecoder().decode(token.trim());
      String str = new String(decode, StandardCharsets.UTF_8);
      return str.split(":");
    }
    return null;
  }

  /**
   * 是否为请求体数据
   */
  public static boolean isMultiPart() {
    return isMultiPart(getRequest());
  }

  /**
   * 是否为请求体数据
   */
  public static boolean isMultiPart(HttpServletRequest request) {
    String contentType = request.getHeader("content-type");
    return StringUtils.isNotBlank(contentType) && contentType.startsWith("multipart/form-data; boundary=");
  }

  /**
   * 获取当前请求的参数
   */
  public static Map<String, String[]> getParameterMap() {
    return getParameterMap(getRequest());
  }

  /**
   * 获取当前请求的参数
   */
  public static Map<String, String[]> getParameterMap(HttpServletRequest request) {
    return request.getParameterMap();
  }

  /**
   * 将请求的参数转换为JSON
   */
  public static JSONObject parametersToJSON(HttpServletRequest request) {
    return parametersToJSON(getParameterMap(request));
  }

  /**
   * 将请求的参数转换为JSON
   */
  public static JSONObject parametersToJSON(Map<String, String[]> map) {
    JSONObject json = new JSONObject(new LinkedHashMap<>());
    map.forEach((k, v) -> {
      if (v == null) json.put(k, null);
      else if (v.length == 1) json.put(k, v[0]);
      else if (v.length > 1) json.put(k, JSON.parseArray(JSON.toJSONString(v)));
    });
    return json;
  }

  /**
   * 获取当前请求的 parts
   */
  public static Map<String, List<Part>> getParts() {
    return getParts(getRequest());
  }

  public static InputStream getInputStream(Part part) {
    try {
      return part.getInputStream();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 获取当前请求的 parts
   */
  public static Map<String, List<Part>> getParts(HttpServletRequest request) {
    try {
      if (!isMultiPart(request)) return new LinkedHashMap<>();
      final Map<String, List<Part>> map = new LinkedHashMap<>();
      for (Part part : request.getParts()) {
        List<Part> values = map.get(part.getName());
        if (values == null) map.put(part.getName(), values = new ArrayList<>());
        values.add(part);
      }
      return map;
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 获取当前请求的 parts 转换为表单的数据
   */
  public static Map<String, List<String>> getForms() {
    return getForms(getRequest());
  }

  /**
   * 获取当前请求的 parts 转换为表单的数据
   */
  public static Map<String, List<String>> getForms(HttpServletRequest request) {
    final Map<String, List<String>> map = new LinkedHashMap<>();
    getParts(request).forEach((name, parts) -> {
      List<String> values = parts.stream()
          .filter(part -> StringUtils.isBlank(part.getSubmittedFileName()))
          .map(part -> IOUtils.readAsString(getInputStream(part), StandardCharsets.UTF_8))
          .collect(Collectors.toList());
      if (!values.isEmpty()) map.put(name, values);
    });
    return map;
  }

  /**
   * 获取当前请求上传的文件
   */
  public static Map<String, List<MultipartFile>> getFiles() {
    return getFiles(getRequest());
  }

  /**
   * 获取当前请求上传的文件
   */
  public static Map<String, List<MultipartFile>> getFiles(HttpServletRequest request) {
    final Map<String, List<MultipartFile>> map = new LinkedHashMap<>();
    getParts(request).forEach((name, parts) -> {
      List<MultipartFile> values = parts
          .stream()
          .filter(part -> StringUtils.isNotBlank(part.getSubmittedFileName()))
          .map(StandardMultipartFile::new)
          .collect(Collectors.toList());
      if (!values.isEmpty()) map.put(name, values);
    });
    return map;
  }

  /**
   * 获取当前请求的IP地址
   *
   * @return 返回IP地址
   */
  public static String getIp() {
    return getIp(getRequest());
  }

  /**
   * 获取IP地址
   *
   * @param request 请求
   * @return 返回IP地址
   */
  public static String getIp(HttpServletRequest request) {
    String ip = request.getHeader("x_forward_for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      request.getHeader("X-Forwarded-For");
    }

    if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
      // 多次反向代理后会有多个ip值，第一个ip才是真实ip
      int index = ip.indexOf(",");
      return index > 0 ? ip.substring(0, index) : ip;
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("x-real_ip");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Forwarded-For");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("host").split(":")[0];
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    if (StringUtils.isBlank(ip)) {
      ip = request.getRemoteHost();
    }
    return ip;
  }

  /**
   * 获取请求信息
   */
  public static Map<String, Object> getRequestInfo() {
    return getRequestInfo(getRequest());
  }

  /**
   * 获取请求信息
   */
  public static Map<String, Object> getRequestInfo(HttpServletRequest request) {
    Map<String, Object> infoMap = new LinkedHashMap<>();
    infoMap.put("remoteHost", request.getRemoteHost());
    infoMap.put("remotePort", request.getRemotePort());
    infoMap.put("contextPath", request.getContextPath());
    infoMap.put("servletPath", request.getServletPath());
    infoMap.put("characterEncoding", request.getCharacterEncoding());
    infoMap.put("contentLength", request.getContentLength());
    infoMap.put("dispatcherType", request.getDispatcherType());
    infoMap.put("requestURI", request.getRequestURI());
    infoMap.put("method", request.getMethod());
    infoMap.put("header", getHeaderMap(request));
    infoMap.put("queryString", request.getQueryString());
    infoMap.put("parameterMap", request.getParameterMap());
    infoMap.put("attributeNames", request.getAttributeNames());
    infoMap.put("pathInfo", request.getPathInfo());
    infoMap.put("authType", request.getAuthType());
    infoMap.put("cookies", request.getCookies());
    infoMap.put("remoteUser", request.getRemoteUser());
    infoMap.put("requestedSessionId", request.getRequestedSessionId());
    infoMap.put("trailerFields", request.getTrailerFields());
    infoMap.put("locale", request.getLocale());
    infoMap.put("locales", request.getLocales());
    return infoMap;
  }

  /**
   * 获取请求体
   *
   * @param request 请求
   */
  public static byte[] getBody(ServletRequest request) {
    try {
      return IOUtils.readFully(request.getInputStream()).toByteArray();
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 获取请求体
   *
   * @param request 请求
   */
  public static String getBodyAsString(ServletRequest request) {
    return getBodyAsString(request, request.getCharacterEncoding());
  }

  /**
   * 获取请求体
   *
   * @param request           请求
   * @param characterEncoding 编码
   */
  public static String getBodyAsString(ServletRequest request, String characterEncoding) {
    try {
      return IOUtils.readFully(request.getInputStream()).toString(characterEncoding);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 响应
   *
   * @param response   HttpServletResponse
   * @param statusCode 状态码
   * @param body       数据
   * @return 返回 HttpServletResponse
   */
  public static HttpServletResponse write(HttpServletResponse response, int statusCode, String body) {
    return write(response, statusCode, body.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 响应
   *
   * @param response   HttpServletResponse
   * @param statusCode 状态码
   * @param body       数据
   * @return 返回 HttpServletResponse
   */
  public static HttpServletResponse write(HttpServletResponse response, int statusCode, byte[] body) {
    response.setStatus(statusCode);
    try {
      write(response.getOutputStream(), body);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
    return response;
  }

  /**
   * 响应
   *
   * @param body 数据
   */
  public static void write(OutputStream out, byte[] body) {
    try {
      out.write(body);
      out.flush();
    } catch (Exception e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 响应文件
   *
   * @param response HTTP响应
   * @param file     返回的文件
   */
  public static void write(HttpServletResponse response, File file) {
    response.setContentType("application/octet-stream");
    response.setContentLengthLong(file.length());
    response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes(), StandardCharsets.ISO_8859_1));
    try {
      IOUtils.write(file, response.getOutputStream(), false);
    } catch (Exception e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }


  // --------------------------------------------------------------------------------------------------
  // 断点续传

  /**
   * 支持断点续传的上传方式
   *
   * @param request 请求
   * @param source  上传的文件
   * @param target  保存的文件
   * @return 返回上传的长度
   * @throws IOException IO异常
   */
  public static RangeSettings upload(@Nullable HttpServletRequest request,
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
  public static RangeSettings download(HttpServletRequest request,
                                       HttpServletResponse response,
                                       final File source,
                                       final String filename) throws IOException {
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
  public static RangeSettings download(HttpServletRequest request,
                                       HttpServletResponse response,
                                       final File source,
                                       final String filename,
                                       final boolean acceptRanges) throws IOException {
    if (!source.exists()) {
      throw new FileNotFoundException(source.getAbsolutePath());
    }

    final RangeSettings settings = setResponseHeaders(request, response, source, filename, acceptRanges);
    try (final BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(source.toPath()));) {
      // 跳过 n 个字节
      bis.skip(settings.getStart());
      final ServletOutputStream out = response.getOutputStream();
      transferTo(bis, settings, (buff, len) -> out.write(buff, 0, len));
      out.flush();
    } catch (ClientAbortException e) {
      // 客户端被强制关闭，
      log.error("throws: {}", e.getMessage());
    }
    return settings;
  }

  private static void transferTo(BufferedInputStream bis, RangeSettings settings, BiConsumer<byte[], Integer> consumer) throws IOException {
    long count = 0;
    try {
      final byte[] buf = new byte[1024 << 4];
      int len;
      while (count < settings.getContentLength()) {
        len = bis.read(buf, 0, (int) Math.min(buf.length, settings.getContentLength() - count));
        count += len;
        consumer.accept(buf, len);
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
   * @return 返回设置
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
  public static RangeSettings parseRangeHeader(@Nullable HttpServletRequest request, long totalLength, boolean acceptRanges) {
    request = request != null ? request : getRequest();
    if (request == null) {
      throw new IllegalArgumentException("request对象不能为null");
    }
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
    response.addHeader("Content-Disposition", "attachment;filename=" +
        new String(filename.getBytes(), StandardCharsets.ISO_8859_1));
    // set the MIME type.
    response.setContentType(ContentType.get(filename));
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


  static class StandardMultipartFile implements MultipartFile, Serializable {

    private final Part part;

    public StandardMultipartFile(Part part) {
      this.part = part;
    }

    @Override
    public String getName() {
      return this.part.getName();
    }

    @Override
    public String getOriginalFilename() {
      return this.part.getSubmittedFileName();
    }

    @Override
    public String getContentType() {
      return this.part.getContentType();
    }

    @Override
    public boolean isEmpty() {
      return (this.part.getSize() == 0);
    }

    @Override
    public long getSize() {
      return this.part.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
      return IOUtils.readAsBytes(this.part.getInputStream());
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return this.part.getInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
      this.part.write(dest.getPath());
      if (dest.isAbsolute() && !dest.exists()) {
        // Servlet 3.0 Part.write is not guaranteed to support absolute file paths:
        // may translate the given path to a relative location within a temp dir
        // (e.g. on Jetty whereas Tomcat and Undertow detect absolute paths).
        // At least we offloaded the file from memory storage; it'll get deleted
        // from the temp dir eventually in any case. And for our user's purposes,
        // we can manually copy it to the requested location as a fallback.
        IOUtils.write(this.part.getInputStream(), Files.newOutputStream(dest.toPath()));
      }
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
      IOUtils.write(this.part.getInputStream(), Files.newOutputStream(dest));
    }
  }
}
