package com.hsrg.collectorrelay.parse;

/**
 * 开关类型
 */
public enum SwitchType {

  /**
   * 体温计断开蓝灯闪烁开关
   */
  @Deprecated
  THERMOMETER((byte) 0x01, (byte) 0xfe),

  /**
   * 体温计电量低绿灯闪烁
   */
  @Deprecated
  THERMOMETER_LOW((byte) 0x08, (byte) 0xf7),

  /**
   * 锂电池电量低绿灯闪烁开关
   */
  BATTERY_LOW((byte) 0x02, (byte) 0xfd),

  /**
   * 锂电池电量低震动关闭
   */
  BATTERY_VIBRATED((byte) 0x04, (byte) 0xfb),

  /**
   * 蓝牙体温计开关控制位
   */
  THERMOMETER_BT((byte) 0x10, (byte) 0xef),

  /**
   * 蓝牙血氧计开关控制位
   */
  BLOOD_OXYGEN((byte) 0x20, (byte) 0xdf),

  /**
   * 蓝牙血压计开关控制位，电子血压计，已过时
   */
  @Deprecated
  SPHYGMOMANOMETER((byte) 0x40, (byte) 0xbf),

  /**
   * 蓝牙流速仪开关控制位
   */
  FLOWMETER((byte) 0x80, (byte) 0x7f);

  private final byte on;
  private final byte off;

  SwitchType(byte on, byte off) {
    this.on = on;
    this.off = off;
  }

  public byte getOn() {
    return on;
  }

  public byte getOff() {
    return off;
  }
}
