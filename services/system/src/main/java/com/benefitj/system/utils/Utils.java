//package com.benefitj.mybatisplus.utils;
//
//import org.apache.commons.lang3.StringUtils;
//
//public class Utils {
//
//  /**
//   * 是否以检查的后缀开头
//   *
//   * @param str    字符串
//   * @param suffix 后缀
//   * @return 返回匹配结果
//   */
//  public static boolean isEndWiths(String str, String suffix) {
//    return str != null && str.endsWith(suffix);
//  }
//
//  /**
//   * 以要求的后缀结尾
//   *
//   * @param str    字符串
//   * @param suffix 后缀
//   * @return 返回拼接的字符串
//   */
//  public static String endWiths(String str, String suffix) {
//    if (str != null) {
//      if (!isEndWiths(str, suffix)) {
//        return str + suffix;
//      }
//      return str;
//    }
//    return suffix;
//  }
//
//  /**
//   * 是否以检查的前缀开头
//   *
//   * @param str    字符串
//   * @param prefix 前缀
//   * @return 返回匹配结果
//   */
//  public static boolean isStartWiths(String str, String prefix) {
//    return str != null && str.startsWith(prefix);
//  }
//
//  /**
//   * 以要求的前缀结尾
//   *
//   * @param str    字符串
//   * @param prefix 前缀
//   * @return 返回拼接的字符串
//   */
//  public static String startWiths(String str, String prefix) {
//    if (str != null) {
//      if (!isStartWiths(str, prefix)) {
//        return str + prefix;
//      }
//      return str;
//    }
//    return prefix;
//  }
//
//  /**
//   * 以要求的前缀结尾
//   *
//   * @param str    字符串
//   * @param prefix 前缀
//   * @param suffix 后缀
//   * @return 返回拼接的字符串
//   */
//  public static String withs(String str, String prefix, String suffix) {
//    if (str != null) {
//      if (StringUtils.isEmpty(prefix) && !isStartWiths(str, prefix)) {
//        str = str + prefix;
//      }
//      if (StringUtils.isEmpty(suffix) && !isEndWiths(str, suffix)) {
//        str = str + suffix;
//      }
//      return str;
//    }
//    str = startWiths(str, prefix);
//    str = endWiths(str, suffix);
//    return str;
//  }
//
//}
