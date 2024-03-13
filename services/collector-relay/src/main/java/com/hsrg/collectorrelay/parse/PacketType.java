package com.hsrg.collectorrelay.parse;

import javax.annotation.Nullable;

/**
 * 数据类型
 */
public enum PacketType {

  /**
   * 注册
   */
  REGISTER(0x01 & 0xFF),
  /**
   * 实时数据
   */
  REALTIME(0x03 & 0xFF),
  /**
   * 肺康复的数据包，包含流速仪数据
   */
  REALTIME2(0xF3 & 0xFF),
  /**
   * 集中上传通用数据包
   */
  FAST_UPLOAD(0x10 & 0xFF),
  /**
   * 集中上传通用数据包(响应)
   */
  FEEDBACK_FAST_UPLOAD(0x43 & 0xFF),
  /**
   * 丢包重传
   */
  PACKET_RETRY(0x08 & 0xFF),
  /**
   * 丢包重传(响应)
   */
  FEEDBACK_PACKET_RETRY(0x83 & 0xFF),
  /**
   * 设置时间反馈
   */
  FEEDBACK_SET_TIME(0x09 & 0xFF),
  /**
   * 删除日志文件反馈 10
   */
  FEEDBACK_DELETE_LOG(0x0A & 0xFF),
  /**
   * 状态开关提示
   */
  FEEDBACK_SWITCH_STATUS(0x0B & 0xFF),
  /**
   * 收到注销反馈包
   */
  UNREGISTER(0x0C & 0xFF),
  /**
   * 蓝牙配置反馈
   */
  FEEDBACK_BLUETOOTH(0x0D & 0xFF),
  /**
   * 实时数据反馈
   */
  FEEDBACK_REALTIME(0x04 & 0xFF),
  /**
   * 开始测量血压
   */
  BLOOD_PRESSURE_MEASURE(0x0E & 0xFF),
  /**
   * 血压采集数据包
   */
  BLOOD_PRESSURE_DATA(0x0F & 0xFF),
  /**
   * 5bl1 采集控制指令反馈
   */
  @Deprecated
  _5BL1_FEEDBACK(0x10 & 0xFF),
  /**
   * 5bl1 设备心跳
   */
  @Deprecated
  _5BL1_HEARTBEAT(0x11 & 0xFF),
  /**
   * 固件升级
   */
  FEEDBACK_UPGRADE(0x20 & 0xFF),
  /**
   * 模拟
   */
  SIMULATE(0xEE),

  //~
  ;

  private final int flag;

  PacketType(int flag) {
    this.flag = flag;
  }

  public int getFlag() {
    return flag;
  }

  public boolean isType(PacketType type) {
    return this == type;
  }

  /**
   * 获取包类型
   *
   * @param flag 类型值
   * @return 返回包类型
   */
  @Nullable
  public static PacketType valueOf(int flag) {
    for (PacketType type : values()) {
      if (type.flag == flag) {
        return type;
      }
    }
    return null;
  }

  public static PacketType valueOf(byte flag) {
    return valueOf(flag & 0xFF);
  }

  /**
   * 是否是支持的类型
   *
   * @param flag 类型值
   * @return 返回是否支持
   */
  public static boolean isLegal(int flag) {
    for (PacketType type : values()) {
      if (type.flag == flag) {
        return true;
      }
    }
    return false;
  }

  /**
   * 是否是支持的类型
   *
   * @param flag 类型值
   * @return 返回是否支持
   */
  public static boolean isLegal(byte flag) {
    return isLegal(flag & 0xFF);
  }

  /**
   * 判断是否为数据，如果为实时数据，或丢包重传，或快速上传数据，返回true
   *
   * @param type 类型
   * @return 返回是否为数据
   */
  public static boolean isData(PacketType type) {
    return isRealtime(type) || (type == FEEDBACK_PACKET_RETRY) || (type == FEEDBACK_FAST_UPLOAD);
  }

  /**
   * 判断是否为数据，如果为实时数据，或丢包重传，返回true
   *
   * @param flag 类型值
   * @return 返回是否为数据
   */
  public static boolean isData(int flag) {
    return isData(valueOf(flag));
  }

  /**
   * 判断是否为数据，如果为实时数据，或丢包重传，返回true
   *
   * @param flag 类型值
   * @return 返回是否为数据
   */
  public static boolean isData(byte flag) {
    return isData(flag & 0xFF);
  }

  /**
   * 判断是否为实时数据
   *
   * @param type 类型值
   * @return 返回是否为实时数据
   */
  public static boolean isRealtime(PacketType type) {
    return (type == REALTIME) || (type == REALTIME2);
  }

  /**
   * 判断是否为实时数据
   *
   * @param flag 类型值
   * @return 返回是否为实时数据
   */
  public static boolean isRealtime(int flag) {
    return flag == REALTIME.flag || flag == REALTIME2.flag;
  }

  /**
   * 判断是否为实时数据
   *
   * @param flag 类型值
   * @return 返回是否为实时数据
   */
  public static boolean isRealtime(byte flag) {
    return isRealtime(flag & 0xFF);
  }

  /**
   * 判断是否包含流速仪数据
   *
   * @param flag 类型值
   * @return 返回是否包含流速仪数据
   */
  public static boolean isFlowmeter(byte flag) {
    return isFlowmeter(flag & 0xFF);
  }

  /**
   * 判断是否包含流速仪数据
   *
   * @param flag 类型值
   * @return 返回是否包含流速仪数据
   */
  public static boolean isFlowmeter(int flag) {
    return REALTIME2.flag == flag;
  }


  public static boolean isType(byte flag, PacketType type) {
    return isType(0xFF & flag, type);
  }

  public static boolean isType(int flag, PacketType type) {
    return flag == type.getFlag();
  }

}
