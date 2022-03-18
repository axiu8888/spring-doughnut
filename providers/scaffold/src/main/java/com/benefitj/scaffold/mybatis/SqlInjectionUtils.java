package com.benefitj.scaffold.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * sql注入处理工具类
 */
@Slf4j
public class SqlInjectionUtils {
  /**
   * sign 用于表字典加签的盐值【SQL漏洞】
   */
  private static final String xssStr = "'|and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+";

  /**
   * sql注入过滤处理，遇到注入关键字抛异常
   */
  public static void filterContent(String... values) {
    String[] xssArr = xssStr.split("\\|");
    for (String value : values) {
      if (StringUtils.isBlank(value)) {
        return;
      }
      // 统一转为小写
      value = value.toLowerCase();
      for (String s : xssArr) {
        if (value.contains(s)) {
          log.error("请注意，存在SQL注入关键词---> {}", s);
          log.error("请注意，值可能存在SQL注入风险!---> {}", value);
          throw new IllegalStateException("请注意，值可能存在SQL注入风险!--->" + value);
        }
      }
    }
  }

}
