package com.hsrg.collectorrelay.parse;

/**
 * 流速仪类型
 */
public enum FlowmeterType {
  /**
   * 是流速仪
   */
  Yes(true),
  /**
   * 不是流速仪
   */
  No(false),
  /**
   * 未知
   */
  UNKNOWN(false)

  ;

  private final boolean flag;

  FlowmeterType(boolean flag) {
    this.flag = flag;
  }

  public boolean isFlag() {
    return flag;
  }

}