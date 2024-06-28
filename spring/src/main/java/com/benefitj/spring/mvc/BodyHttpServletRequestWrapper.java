package com.benefitj.spring.mvc;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.spring.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * 包装请求体
 */
public class BodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

  public static BodyHttpServletRequestWrapper wrap(HttpServletRequest request) {
    return request instanceof BodyHttpServletRequestWrapper
        ? (BodyHttpServletRequestWrapper) request
        : new BodyHttpServletRequestWrapper(request);
  }

  RewriteServletInputStream stream;

  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request The request to wrap
   * @throws IllegalArgumentException if the request is null
   */
  public BodyHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  public RewriteServletInputStream getStream() {
    RewriteServletInputStream is = stream;
    if (is == null) {
      synchronized (this) {
        if ((is = this.stream) == null) {
          is = (this.stream = new RewriteServletInputStream(getRequest()));
        }
      }
    }
    return is;
  }

  @Override
  public ServletInputStream getInputStream() {
    return getStream();
  }

  public void reset() {
    try {
      getInputStream().reset();
    } catch (IOException ignore) {/* ignore */}
  }


  public void setNewInput(String content) {
    try {
      getStream().setNewInput(content.getBytes(getCharacterEncoding()));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  public void setNewInput(byte[] content) {
    getStream().setNewInput(content);
  }

  @Override
  public int getContentLength() {
    //return super.getContentLength();
    return getStream().getInput().available();
  }

  @Override
  public long getContentLengthLong() {
    return super.getContentLengthLong();
  }

  public static class RewriteServletInputStream extends ServletInputStream {

    ByteArrayInputStream input;

    ReadListener listener;

    public RewriteServletInputStream() {
    }

    public RewriteServletInputStream(ByteArrayInputStream input) {
      this.setInput(input);
    }

    public RewriteServletInputStream(ServletRequest request) {
      this(new ByteArrayInputStream(ServletUtils.getBody(request)));
    }

    public void setInput(ByteArrayInputStream input) {
      this.input = input;
      input.mark(0);
    }

    public ByteArrayInputStream getInput() {
      return input;
    }

    public void setNewInput(String content) {
      setNewInput(content.getBytes(StandardCharsets.UTF_8));
    }

    public void setNewInput(byte[] content) {
      setInput(new ByteArrayInputStream(content));
    }

    @Override
    public boolean isFinished() {
      return getInput().available() > 0;
    }

    @Override
    public boolean isReady() {
      return getInput().available() > 0;
    }

    @Override
    public void setReadListener(ReadListener listener) {
      this.listener = listener;
    }

    @Override
    public int read() throws IOException {
      return getInput().read();
    }

    @Override
    public synchronized void reset() {
      getInput().reset();
    }

    public JSONObject toJson() {
      String str = IOUtils.readFully(getInput()).toString();
      return StringUtils.isNotBlank(str) ? JSON.parseObject(str) : new JSONObject();
    }

  }

}
