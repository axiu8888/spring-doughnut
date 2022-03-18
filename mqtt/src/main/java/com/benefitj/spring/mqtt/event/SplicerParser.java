package com.benefitj.spring.mqtt.event;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串拼接器解析
 */
public class SplicerParser {

  private static final Pattern pattern = Pattern.compile("\\#\\{\\w+\\}");

  /**
   * 解析
   *
   * @param input 输入的字符串
   * @return 返回字符串拼接器
   */
  public static List<Splicer> parse(String input) {
    List<Splicer> splicers = new LinkedList<>();
    synchronized (SplicerParser.class) {
      int index = 0;
      Matcher m = pattern.matcher(input);
      while (m.find()) {
        if (m.start() > index) {
          index = addSplicer(splicers, input.substring(index, m.start()), index, false);
        }
        index = addSplicer(splicers, m.group(), index, true);
      }
      if (index < input.length()) {
        index = addSplicer(splicers, input.substring(index), index, false);
      }
    }
    return splicers;
  }

  static int addSplicer(List<Splicer> splicers, String segment, int start, boolean placeholder) {
    splicers.add(Splicer.builder()
        .segment(segment)
        .start(start)
        .end(start + segment.length())
        .name(placeholder ? segment.substring(segment.indexOf("{") + 1, segment.indexOf("}")) : null)
        .placeholder(placeholder)
        .build());
    return start + segment.length();
  }

}
