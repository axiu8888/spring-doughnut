package com.benefitj.scaffold.mybatis;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryFill {

  /**
   * 获取sql中的#{key} 这个key组成的set
   */
  public static Set<String> getSqlRuleParams(String sql) {
    if (StringUtils.isBlank(sql)) {
      return null;
    }
    Set<String> varParams = new HashSet<String>();

    Pattern p = Pattern.compile("\\#\\{\\w+\\}");
    Matcher m = p.matcher(sql);
    while (m.find()) {
      String var = m.group();
      varParams.add(var.substring(var.indexOf("{") + 1, var.indexOf("}")));
    }
    return varParams;
  }
}
