package com.hsrg.collectorrelay.parse.bean;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 采集器采集的数据：实时数据或重传数据，目前 “电子血压计” 和 “体温计” 都未使用
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class HardwarePacket {

  /**
   * 包长度
   */
  int packetLength;
  /**
   * 包类型
   */
  int type;
  /**
   * 设备ID，16进制的设备ID，参考{@link #deviceCode}
   */
  String deviceId;
  /**
   * 设备号，4个字节的设备编码，参考{@link #deviceId}
   */
  int deviceCode;
  /**
   * 版本名
   */
  String versionName;
  /**
   * 版本号：整形的数值
   */
  int versionCode;
  /**
   * 是否实时数据包
   */
  @Builder.Default
  boolean realtime = true;
  /**
   * 包序号
   */
  int packetSn;
  /**
   * 设备时间
   */
  String deviceDate;
  /**
   * 设备时间戳
   */
  long time;
  /**
   * 心电信号
   */
  int[] ecgList;
  /**
   * 三轴：X轴
   */
  int[] xList;

  /**
   * 三轴：Y轴
   */
  int[] yList;
  /**
   * 三轴：Z轴
   */
  int[] zList;
  /**
   * 呼吸list
   */
  int[] rawRespList;
  /**
   * 腹呼吸list
   */
  int[] rawAbdominalRespList;
  /**
   * 血氧list
   */
  int[] spo2List;

  /**
   * 体温计时间
   */
  @Deprecated
  String temperatureTime;
  /**
   * 体温
   */
  @Deprecated
  short temperature;
  /**
   * 脉率
   */
  short pulseRate;
  /**
   * 设备功耗过高标识
   */
  byte deviceOverload;
  /**
   * 胸呼吸连接状态
   */
  byte respConnState;
  /**
   * 腹呼吸连接状态
   */
  byte abdominalConnState;
  /**
   * 血氧信号强度
   */
  byte spo2Signal;
  /**
   * 胸呼吸系数
   */
  short respRatio;
  /**
   * 腹呼吸系数
   */
  short abdominalRatio;
  /**
   * 血氧饱和度
   */
  byte spo2;
  /**
   * 开机数据包 1：开机数据包 0：非开机数据包
   */
  byte powerOn;
  /**
   * 时间校准参数，如果为1则需要发送时间校准指令
   */
  byte calibrationTime;
  /**
   * 电子血压计连接状态
   */
  @Deprecated
  byte elecMmhgConnState;
  /**
   * 血氧设备连接状态 0正常 1告警
   */
  byte spo2ConnState;
  /**
   * 体温设备连接状态
   */
  @Deprecated
  byte temperatureConnState;
  /**
   * 血氧设备探头连接状态
   */
  byte spo2ProbeConnState;
  /**
   * 心电导联脱落状态
   */
  byte ecgConnState;
  /**
   * 流速仪连接断开标识
   */
  byte flowmeterConnState;
  /**
   * 流速仪电量低告警
   */
  byte flowmeterBatteryAlarm;
  /**
   * 外部电池电量低告警
   */
  byte deviceOuterBatteryAlarm;
  /**
   * 蓝牙体温计电池电量低告警
   */
  @Deprecated
  byte temperatureBatteryAlarm;
  /**
   * 蓝牙血氧电池电量告警
   */
  @Deprecated
  byte spo2BatteryAlarm;
  /**
   * 蓝牙血压计电池电量低告警
   */
  @Deprecated
  byte elecMmhgBatteryAlarm;

  // ----------------开关  0-关 1-开
  /**
   * 流速计开关
   */
  byte flowmeterSwitch;
  /**
   * 血压计开关
   */
  @Deprecated
  byte elecMmhgSwitch;
  /**
   * 蓝牙血氧计开关
   */
  byte spo2Switch;
  /**
   * 蓝牙体温计开关
   */
  @Deprecated
  byte temperatureSwitch;
  /**
   * 锂电池电量低绿闪开关
   */
  byte batteryLowLightSwitch;
  /**
   * 锂电池电量低震动开关
   */
  byte batteryLowShockSwitch;
  /**
   * 蓝牙设备电量低绿闪
   */
  byte bluetoothLightSwitch;
  /**
   * 蓝牙设备连接断开蓝闪
   */
  byte bluetoothConnSwitch;

  // ----------------电量------------------
  /**
   * 设备电量
   */
  short deviceBattery;
  /**
   * 体温计电量
   */
  @Deprecated
  short temperatureBattery;
  /**
   * 血氧电量
   */
  short spo2Battery;
  /**
   * 设备外部电池电量
   */
  short deviceOuterBattery;
  /**
   * 电子血压计电量
   */
  @Deprecated
  short elecMmhgBattery;
  /**
   * 流速计电量
   */
  short flowmeterBattery;
  /**
   * wifi信号强度
   */
  short wifiSignal;
  /**
   * 设备连接wifi热点的mac
   */
  String apMac;

  /**
   * 是否启用肺康复的设备
   */
  boolean flowmeter;
  /**
   * 是否是呼气
   */
  int[] breath;
  /**
   * 实时流速
   */
  int[] realtimeFlowVelocity;
  /**
   * 实时容积
   */
  int[] realtimeVolume;

  /**
   * 电池电量格数
   */
  int batteryLevelGridCount;

  /**
   * 质子体温
   */
  Short carepatchTemperature;
  /**
   * 质子体温包序号
   */
  Integer carepatchSn;

  // ===========================================================================


  String id;

  /**
   * 患者ID
   */
  String personZid;
  /**
   * 病区号
   */
  String wardCode;
  /**
   * 床号
   */
  int orderNum;

  ///*************************************************/

  /**
   * 心率
   */
  int heartRate;
  /**
   * 呼吸率
   */
  int respRate;
  /**
   * 体位
   */
  int gesture;
  /**
   * 步数
   */
  int step;
  /**
   * 能量，卡路里
   */
  double energy;
  /**
   * 是否跌倒( 0 / 1 )
   */
  int fall;
  /**
   * 运动趋势
   */
  int sportsTrend;
  /**
   * 潮气量，已过时，目前是单独计算
   */
  @Deprecated
  short volume;

  /**
   * 心率告警
   */
  byte heartRateAlarm;
  /**
   * 呼吸率告警
   */
  byte respRateAlarm;
  /**
   * 脉率告警
   */
  byte plusRateAlarm;
  /**
   * 体温告警
   */
  byte temperatureAlarm;
  /**
   * 血氧告警
   */
  byte spo2Alarm;
  /**
   * 胸部呼吸，经过滤波过滤后的数据
   */
  int[] respList;
  /**
   * 腹部呼吸，经过滤波过滤后的数据
   */
  int[] abdominalList;

  /**
   * * NORMAL_SINUS_RHYTHM = 1, // 窦性心律
   * * SINUS_TACHYCARDIA = 2, // 窦性心动过速
   * * SINUS_BRADYCARDIA = 3, // 窦性心动过缓
   * *
   * * SUPRAVENTRICULAR_PREMATURE_CONTRACTION = 4, // 室上性期前收缩
   * * PAC_BIGEMINY = 5, // 室上性期前收缩二联律
   * * PAC_TRIGEMINY = 6, // 室上性期前收缩三联律
   * * PAIR_PAC = 7, // 成对室上性期前收缩
   * * SHORT_TUN = 8, // 短阵室上性心动过速
   * * ATRIAL_FIBRILLATION = 9, // 心房颤动
   * * ATRIAL_FLUTTER = 10, // 心房扑动
   * *
   * * PREMATURE_VENTRICULAR_CONTRACTION = 11, // 室性期前收缩
   * * PVC_BIGEMINY = 12, // 室性期前收缩二联律
   * * PVC_TRIGEMINY = 13, // 室性期前收缩三联律
   * * PAIR_PVC = 14, // 成对室性期前收缩
   * * VENTRICULAR_TACHYCARDIA = 15, // 室性心动过速
   * * VENTRICULAR_FIBRILLATION = 16, // 室颤
   * * LONG_RR_INTERVAL = 17, // 长RR间期
   * * BEAT_STOP = 18, // 停搏
   */
  int arrhythmiaType;//心律失常类型

  ///*************************************************/

  /**
   * 肺康复，是否校准,用于在pad端显示校准过的还是呼吸波形
   */
  int calibration;
  /**
   * 实时呼吸比
   */
  int eiRatio;
  /**
   * 实时胸腹呼吸共享比
   */
  int caRatio;
  /**
   * 潮气量
   */
  int[] tidalVolume;

  /**
   * 加速度
   */
  int acceleration;
  /**
   * 加速度拟合值（0-1024）
   */
  int[] xyzOutList;
  /**
   * 呼吸暂停报警  0：无   1：有
   */
  int apnea;
  /**
   * 加圈
   */
  int circle;


  public HardwarePacket(boolean realtime) {
    this.realtime = realtime;
  }

  /**
   * 是否为1A设备
   */
  public boolean is1ADevice() {
    String deviceId = getDeviceId();
    return deviceId != null && (deviceId.startsWith("11") || deviceId.startsWith("16"));
  }

}
