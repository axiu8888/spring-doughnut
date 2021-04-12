package com.benefitj.spring.aop.log;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConditionalOnMissingBean({HttpLoggingCustomizer.class})
@Component
public class SimpleHttpLoggingCustomizer implements HttpLoggingCustomizer {

  protected static final Logger log = LoggerFactory.getLogger(HttpLoggingCustomizer.class);

  /**
   * 是否分多行打印
   */
  @Value("#{ @environment['spring.aop.http-logging.multi-line'] ?: true }")
  private boolean multiLine = true;
  /**
   * 是否打印日志
   */
  @Value("#{ @environment['spring.aop.http-logging.print'] ?: true }")
  private boolean print = true;

  @Override
  public boolean printable() {
    return print;
  }

  @Override
  public void customize(HttpLoggingHandler handler, Map<String, Object> args) {
    final StringBuilder sb = new StringBuilder();
    sb.append(isMultiLine() ? "\n" : "");
    String separator = separator();
    args.forEach((key, value) ->
        sb.append(key)
            .append("[")
            .append(toValue(value))
            .append("]")
            .append(separator)
    );
    sb.replace(sb.length() - separator.length(), sb.length(), "");
    log.info(sb.toString());
  }

  protected String separator() {
    return isMultiLine() ? "\n" : ", ";
  }

  public String toValue(Object o) {
    if (o == null) {
      return "";
    }
    if (o instanceof Number
        || o instanceof Boolean
        || o instanceof CharSequence
        || o instanceof Character) {
      return String.valueOf(o);
    }
    return JSON.toJSONString(o);
  }

  public boolean isMultiLine() {
    return multiLine;
  }

  public void setMultiLine(boolean multiLine) {
    this.multiLine = multiLine;
  }
}
