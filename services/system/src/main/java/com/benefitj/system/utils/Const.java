package com.benefitj.system.utils;

/**
 * 常量
 */
public class Const {

  public static final String AUTHORIZATION = "Authorization";

  public static final String ORG = "org";

  public static String keyAccount(String username) {
    return "users:account:" + username;
  }

  public static String keyUserInfo(String userId) {
    return "users:userinfo:" + userId;
  }

  public static String keyUsername(String userId) {
    return "users:username:" + userId;
  }

}
