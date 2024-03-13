package com.hsrg.collectorrelay.parse;

import com.benefitj.core.HexUtils;
import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.core.local.LocalMapCache;
import com.hsrg.collectorrelay.parse.bean.HardwarePacket;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据包解析，只解析实时数据和重传数据
 */
public class PacketUtils {

  /**
   * 电池电量的类型
   */
  public enum BatteryType {
    /**
     * 0：采集器内部电池
     */
    COLLECTOR_INNER(0),
    /**
     * 1：采集器外部电池
     */
    COLLECTOR_OUTER(1),
    /**
     * 2：体温计电池
     */
    @Deprecated
    THERMOMETER(2),
    /**
     * 3：血氧计电池
     */
    OXIMETER(3),
    /**
     * 4：血压计电池
     */
    @Deprecated
    SPHYGMOMANOMETER(4),
    /**
     * 5： 流速仪
     */
    FLOWMETER(5),
    /**
     * 未知
     */
    UNKNOWN(-1);

    private final int type;

    BatteryType(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }

    public static BatteryType valueOf(byte type) {
      return valueOf(type & 0xFF);
    }

    public static BatteryType valueOf(int type) {
      for (BatteryType bt : values()) {
        if (bt.type == type) {
          return bt;
        }
      }
      return UNKNOWN;
    }
  }

  private static final Short S_ZERO = 0;

  private static final String _yMdHms = "yyyy-MM-dd HH:mm:ss";
  /**
   * 波形高位需要相 & 的位
   */
  private static final byte[] WAVE_BIT = new byte[]{
      (byte) 0b00000011, // 3
      (byte) 0b00001100, // 12
      (byte) 0b00110000, // 48
      (byte) 0b11000000 // 192
  };
  /**
   * 移位
   */
  private static final int[] MOVE = new int[]{0, 2, 4, 6};
  /**
   * 缓存电池电量
   */
  private static final Map<String, Map<BatteryType, Short>> BATTERY_CACHE = new ConcurrentHashMap<>();
  /**
   * 字节缓存
   */
  private static final LocalMapCache<Integer, byte[]> BYTE_CACHE = LocalCacheFactory.newBytesWeakHashMapCache();
  /**
   * 缓存时间格式化对象
   */
  private static final ThreadLocal<Map<String, SimpleDateFormat>> SDF_CACHE = ThreadLocal.withInitial(WeakHashMap::new);
  /**
   * 体温数据缓存
   */
  private static final Map<String, Integer> TEMPERATURE_SN_CACHE = new WeakHashMap<>();

  private static SimpleDateFormat getFmt(String format) {
    return SDF_CACHE.get().computeIfAbsent(format, SimpleDateFormat::new);
  }

  /**
   * 格式化
   *
   * @param time 时间戳
   * @return 返回格式化的数据
   */
  private static String fmt(long time) {
    return fmt(_yMdHms, time);
  }

  /**
   * 格式化
   *
   * @param format 日期格式
   * @param time   时间戳
   * @return 返回格式化的数据
   */
  private static String fmt(String format, Object time) {
    return getFmt(format).format(time);
  }

  /**
   * 获取缓存的字节数组
   *
   * @param size 数组大小
   * @return 返回缓存的字节
   */
  @Nonnull
  private static byte[] getCache(int size) {
    return BYTE_CACHE.computeIfAbsent(size);
  }

  /**
   * 右移位
   *
   * @param b    字节数据
   * @param move 移动位数
   * @return 返回被移位的值
   */
  private static byte rightMove(byte b, int move) {
    int value;
    switch (move) {
      case 0:
        value = (b & 0b00000001);
        break;
      case 1:
        value = (b & 0b00000010);
        break;
      case 2:
        value = (b & 0b00000100);
        break;
      case 3:
        value = (b & 0b00001000);
        break;
      case 4:
        value = (b & 0b00010000);
        break;
      case 5:
        value = (b & 0b00100000);
        break;
      case 6:
        value = (b & 0b01000000);
        break;
      case 7:
        value = (b & 0b10000000);
        break;
      default:
        throw new UnsupportedOperationException();
    }
    return (byte) (value >>> move);
  }

  /**
   * 获取设备对应的电池电量缓存
   *
   * @param deviceId 设备ID
   * @return 返回缓存的Map
   */
  public static Map<BatteryType, Short> getBatteryLevelCache(String deviceId) {
    return deviceId == null
        ? null
        : BATTERY_CACHE.computeIfAbsent(deviceId, id -> new ConcurrentHashMap<>(6));
  }

  /**
   * 保存电池电量数据
   *
   * @param deviceId     设备ID
   * @param type         类型
   * @param batteryLevel 电池电量
   */
  public static void putBatteryLevel(String deviceId, BatteryType type, short batteryLevel) {
    Map<BatteryType, Short> map = getBatteryLevelCache(deviceId);
    if (map != null) {
      map.put(type, batteryLevel);
    }
  }

  /**
   * 获取电池电量
   *
   * @param deviceId 设备ID
   * @param type     类型
   * @return 返回电池电量
   */
  public static short getBatteryLevel(String deviceId, BatteryType type) {
    Map<BatteryType, Short> map = getBatteryLevelCache(deviceId);
    return map != null ? map.getOrDefault(type, S_ZERO) : S_ZERO;
  }

  /**
   * 移除设备对应的电池电量缓存
   *
   * @param deviceId 设备ID
   * @return 返回被移除的电池电量缓存
   */
  public static Map<BatteryType, Short> removeBatteryLevel(String deviceId) {
    return BATTERY_CACHE.remove(deviceId);
  }

  /**
   * 清空设备缓存的电池电量数据
   */
  public static void clearBatteryLevel() {
    BATTERY_CACHE.clear();
  }

  /**
   * 判断是否为UDP的数据包
   *
   * @param data 数据
   * @return 返回判断的结果
   */
  public static boolean isUdp(byte[] data) {
    return CollectorHelper.verify(data);
  }

  /**
   * 获取读取数据开始的位置
   *
   * @param data 数据
   * @return 返回开始的位置
   */
  public static int getStart(byte[] data) {
    return isUdp(data) ? 9 : 0;
  }

  /**
   * 解析时间
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回时间
   */
  public static long getTime(byte[] data, int start) {
    // 获取设备时间: (4 ~ 9)
    return CollectorHelper.getTime(data, 4 + start, 4);
  }

  /**
   * 解析
   *
   * @param data   待解析的数据
   * @param packet 解析后的数据
   * @return 返回解析的采集器数据
   */
  public static <T extends HardwarePacket> T parse(byte[] data, T packet) {
    return parse(data, packet, true);
  }


  /**
   * 解析
   *
   * @param data      待解析的数据
   * @param packet    解析后的数据
   * @param parseWave 是否解析波形数据
   * @return 返回解析的采集器数据
   */
  public static <T extends HardwarePacket> T parse(byte[] data, T packet, boolean parseWave) {
    String hexDeviceId = isUdp(data) ? CollectorHelper.getHexDeviceId(data) : null;
    return parse(hexDeviceId, data, packet, parseWave);
  }

  /**
   * 解析
   *
   * @param deviceId 设备ID
   * @param data     待解析的数据
   * @param packet   对象
   * @return 返回解析的采集器数据
   */
  public static HardwarePacket parse(String deviceId, byte[] data, HardwarePacket packet) {
    return parse(deviceId, data, packet, true);
  }

  /**
   * 解析
   *
   * @param deviceId  设备ID
   * @param data      待解析的数据
   * @param packet    对象
   * @param parseWave 是否解析波形
   * @return 返回解析的采集器数据
   */
  public static <T extends HardwarePacket> T parse(String deviceId, byte[] data, T packet, boolean parseWave) {
    // 校验是否为UDP数据
    boolean udp = isUdp(data);
    if (udp && deviceId == null) {
      throw new IllegalStateException("请传入设备ID");
    }
    int start = udp ? 9 : 0;
    if (udp) {
      // 包头: (0 ~ 2], 0x55 0xAA ...
      // 数据包长度: (2 ~ 4]
      packet.setPacketLength(CollectorHelper.length(data));
      // 数据类型: (8 ~ 9] ...
      packet.setType(CollectorHelper.getType(data));
    } else {
      packet.setPacketLength(data.length);
      packet.setType(PacketType.REALTIME.getFlag());
    }
    packet.setRealtime(PacketType.isRealtime(packet.getType()));

    // 设备ID: (4 ~ 7)
    packet.setDeviceId(deviceId);
    packet.setDeviceCode(deviceId != null ? Integer.parseInt(deviceId, 16) : 0);

    // 非UDP数据不包含以上片段

    // 包序号: (0 ~ 3)
    packet.setPacketSn(CollectorHelper.getPacketSn(data, start));
    // 获取设备时间: (4 ~ 9)
    long deviceTime = CollectorHelper.getTime(data, start + 4, 4);
    packet.setTime(deviceTime / 1000);
    // 设备时间
    packet.setDeviceDate(fmt(deviceTime));

    if (parseWave) {
      // 解析波形
      if (udp) {
        parseWaves(packet, data, start, PacketType.isFlowmeter(CollectorHelper.getType(data)));
      } else {
        parseWaves(packet, data, start, false);
      }
    }

    // 体温时间: (508, 511)
    long time = CollectorHelper.getTime(data, 508 + start, 4);
    packet.setTemperatureTime(time != 0 ? fmt(time) : null);

    // 参数高位：(512)
    byte paramHigh = data[512 + start];
    // 设备功耗过高标志       (5)
    packet.setDeviceOverload(rightMove(paramHigh, 5));
    // 胸呼吸连接标志( 0 连接 (6)
    packet.setRespConnState(rightMove(paramHigh, 6));
    // 腹呼吸连接标志( 0 连接 (7)
    packet.setAbdominalConnState(rightMove(paramHigh, 7));
    // 血氧信号强度(513)
    packet.setSpo2Signal(data[513 + start]);
    // 胸呼吸系数(514)
    packet.setRespRatio((short) (data[514 + start] & 0xff));
    // 腹呼吸系数(515)
    packet.setAbdominalRatio((short) (data[515 + start] & 0xff));
    // 体温(516)
    packet.setTemperature((short) ((rightMove(paramHigh, 2) << 8) | (data[516 + start] & 0xFF)));
    // 血氧饱和度(517)
    packet.setSpo2(data[517 + start]);

    // 设备状态: (518)   ... 0 为正常; / 1 为告警;
    // 开机标志在开机第一包数据该位置 1,
    // 其他数据包该位置 0;
    // 时间设置标志开机置 1,在接收到时间设备指令后置 0

    byte deviceState = data[518 + start];
    // 心电导联脱落状态
    packet.setEcgConnState(rightMove(deviceState, 0));
    // 血氧探头脱落标志
    packet.setSpo2ProbeConnState(rightMove(deviceState, 1));
    // 体温连接断开标志
    packet.setTemperatureConnState(rightMove(deviceState, 2));
    // 血氧连接断开标志
    packet.setSpo2ConnState(rightMove(deviceState, 3));
    // 血压连接断开标志
    packet.setElecMmhgConnState(rightMove(deviceState, 4));
    // 流速仪连接断开标志
    packet.setFlowmeterConnState(rightMove(deviceState, 5));
    // 时间设置标志
    packet.setCalibrationTime(rightMove(deviceState, 6));
    // 开机标志
    packet.setPowerOn(rightMove(deviceState, 7));

    // 电量提示：(519)   0 为正常; 1 为告警
    byte batteryHint = data[519 + start];
    // 外部电池电量低
    packet.setDeviceOuterBatteryAlarm(rightMove(batteryHint, 0));
    // 蓝牙体温计电量低
    packet.setTemperatureBatteryAlarm(rightMove(batteryHint, 1));
    // 蓝牙血氧电量低
    packet.setSpo2BatteryAlarm(rightMove(batteryHint, 2));
    // 蓝牙血压计电量低
    packet.setElecMmhgBatteryAlarm(rightMove(batteryHint, 3));
    // 流速仪电量低
    packet.setFlowmeterBatteryAlarm(rightMove(batteryHint, 4));

    // 状态开关: (520)，0为关; 1为开
    byte switchState = data[520 + start];
    // 蓝牙连接断开蓝闪
    packet.setBluetoothConnSwitch(rightMove(switchState, 0));
    // 锂电池电量低绿闪
    packet.setBatteryLowLightSwitch(rightMove(switchState, 1));
    // 锂电池电量低震动
    packet.setBatteryLowShockSwitch(rightMove(switchState, 2));
    // 蓝牙设备电量低绿闪
    packet.setBluetoothLightSwitch(rightMove(switchState, 3));
    // 蓝牙体温计开关位
    packet.setTemperatureSwitch(rightMove(switchState, 4));
    // 蓝牙血氧计开关位
    packet.setSpo2Switch(rightMove(switchState, 5));
    // 蓝牙血压计开关位
    packet.setElecMmhgSwitch(rightMove(switchState, 6));
    // 蓝牙流速仪开关位
    packet.setFlowmeterSwitch(rightMove(switchState, 7));

    // 电量: (521)
    if (packet.isRealtime()) {
      BatteryType batteryType = BatteryType.valueOf(data[521 + start]);
      if (batteryType != BatteryType.COLLECTOR_INNER
          && batteryType != BatteryType.COLLECTOR_OUTER) {
        putBatteryLevel(deviceId, batteryType, (short) (data[522 + start] & 0xFF));
      } else {
        double power = Math.floor(((((data[522 + start] & 0xFF) - 15) * 5 + 3200 - 3300) / (float) (4050 - 3300)) * 100);
        putBatteryLevel(deviceId, batteryType, (short) (Math.max(Math.min(power, 100), 0)));
      }
    }
    // 0：内部电池
    packet.setDeviceBattery(getBatteryLevel(deviceId, BatteryType.COLLECTOR_INNER));
    // 1：外部电池
    packet.setDeviceOuterBattery(getBatteryLevel(deviceId, BatteryType.COLLECTOR_OUTER));
    // 2：体温计电池
    packet.setTemperatureBattery(getBatteryLevel(deviceId, BatteryType.THERMOMETER));
    // 3：血氧计电池
    packet.setSpo2Battery(getBatteryLevel(deviceId, BatteryType.OXIMETER));
    // 4：血压计电池
    packet.setElecMmhgBattery(getBatteryLevel(deviceId, BatteryType.SPHYGMOMANOMETER));
    // 5：流速仪
    packet.setFlowmeterBattery(getBatteryLevel(deviceId, BatteryType.FLOWMETER));

    // WiFi信号强度(523)
    packet.setWifiSignal((short) -(data[523 + start] & 0xFF));
    // 脉率 (524)
    packet.setPulseRate((short) ((rightMove(paramHigh, 1) << 8) | (data[524 + start] & 0xFF)));

    // AP MAC (525, 529) ... 被用下面值的取代了
    byte[] apMac = getCache(5);
    System.arraycopy(data, 525 + start, apMac, 0, apMac.length);
    packet.setApMac(HexUtils.bytesToHex(apMac, true));

    if (packet.is1ADevice()) {
      // 体温数据
      Integer temperatureSn = apMac[2] & 0xFF;
      Integer oldTemperatureSn = TEMPERATURE_SN_CACHE.get(deviceId);
      packet.setCarepatchSn(temperatureSn);
      if (oldTemperatureSn != null) {
        if (!temperatureSn.equals(oldTemperatureSn)) {
          short temperature = (short) (((apMac[0] & 0xFF) << 8) | (apMac[1] & 0xFF));
          packet.setCarepatchTemperature(temperature);
          TEMPERATURE_SN_CACHE.put(deviceId, temperatureSn);
        } else {
          packet.setCarepatchTemperature(null);
        }
      } else {
        packet.setCarepatchTemperature(null);
        TEMPERATURE_SN_CACHE.put(deviceId, temperatureSn);
      }
      packet.setTemperature((short) 0);
    }

    // 电池电量格数
    packet.setBatteryLevelGridCount(data[534 + start] & 0xFF);

    // 版本号 (530)
    byte version = data[530 + start];
    if (version != 0) {
      // 高位
      int high = (version & 0b11100000) >>> 5;
      // 中位
      int middle = (version & 0b00011100) >>> 2;
      // 低位
      int low = version & 0b00000011;
      // 固件版本
      packet.setVersionCode((high << 5) | (middle << 2) | low);
      packet.setVersionName(String.format("%d.%d.%d", high, middle, low));
    }

    return packet;
  }

  /**
   * 解析波形
   *
   * @param packet    实体对象
   * @param data      数据
   * @param flowmeter 是否计算流速仪数据
   */
  public static HardwarePacket parseWaves(HardwarePacket packet, byte[] data, int start, boolean flowmeter) {
    // 胸呼吸波形: (10 ~ 59) => 50
    packet.setRawRespList(parseIntArray(data, start + 10, start + 60, 2));
    // 腹呼吸波形: (60 ~ 109) => 50
    packet.setRawAbdominalRespList(parseIntArray(data, start + 60, start + 110, 2));
    // 心电波形: (110 ~ 361) => [4 * (50 + 13) = 252]
    packet.setEcgList(parseEcgWave(data, start + 110));
    // 加速度波形数据 (362, 456) => 96
    // X轴 (362, 393) => [25 + 7 = 32]
    packet.setXList(parseXyzWave(data, start + 362));
    // Y轴 (394, 425) => [25 + 7 = 32]
    packet.setYList(parseXyzWave(data, start + 394));
    // Z轴 (426, 457) => [25 + 7 = 32]
    packet.setZList(parseXyzWave(data, start + 426));
    // 血氧波形: (458, 507) => 50
    packet.setSpo2List(parseIntArray(data, start + 458, start + 508, 1));

    // 包含流速仪数据: (544, 668)
    packet.setFlowmeter(flowmeter);
    if (flowmeter) {
      // 流速仪第0组数据 (544, 549]
      // 25组，
      // 第一组:
      // 吹气或呼吸(0/1)，1个字节(544)
      // 实时流速值 ml/s，2个字节(545, 547]
      // 实时容积 ml，2个字节(547, 549]
      int[] breath = new int[25];
      int[] realtimeFlowVelocity = new int[25];
      int[] realTimeVolume = new int[25];
      for (int i = 0, j = start + 544; i < 25; i++, j += 5) {
        breath[i] = data[i + j] & 0xFF;
        realtimeFlowVelocity[i] = HexUtils.bytesToInt(data[i + j + 1], data[i + j + 2]);
        realTimeVolume[i] = HexUtils.bytesToInt(data[i + j + 3], data[i + j + 4]);
      }
      packet.setBreath(breath);
      packet.setRealtimeFlowVelocity(realtimeFlowVelocity);
      packet.setRealtimeVolume(realTimeVolume);
    }

    return packet;
  }

  /**
   * 解析成整形类型的字节数组
   *
   * @param data    数据
   * @param start   开始位置
   * @param end     结束位置
   * @param bitSize 每个数据占几个字节
   * @return 返回计算后的数组
   */
  public static int[] parseIntArray(byte[] data, int start, int end, int bitSize) {
    if (bitSize > 0) {
      int[] array = new int[(end - start) / bitSize];
      byte[] buff = bitSize > 1 ? getCache(bitSize) : null;
      for (int i = 0, j = start; i < array.length; i++, j += bitSize) {
        if (bitSize == 1) {
          array[i] = (data[j + 1] & 0xFF);
        } else {
          System.arraycopy(data, j, buff, 0, bitSize);
          array[i] = HexUtils.bytesToInt(buff);
        }
      }
      return array;
    }
    throw new IllegalArgumentException("bitSize >= 1");
  }

  /**
   * 解析成短整形类型的字节数组
   *
   * @param data    数据
   * @param start   开始位置
   * @param end     结束位置
   * @param bitSize 每个数据占几个字节
   * @return 返回计算后的数组
   */
  public static short[] parseShortArray(byte[] data, int start, int end, int bitSize) {
    int[] intArray = parseIntArray(data, start, end, bitSize);
    short[] array = new short[intArray.length];
    for (int i = 0; i < intArray.length; i++) {
      array[i] = (short) intArray[i];
    }
    return array;
  }

  /**
   * 解析心电波形数据
   *
   * @param data  原始数据
   * @param start 开始位置
   * @return 返回解析后的心电数据
   */
  public static int[] parseEcgWave(byte[] data, int start) {
    return parseWave(4, 50, 13, data, start);
  }

  /**
   * 解析三轴加速度波形数据
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的三轴波形数据
   */
  public static int[] parseXyzWave(byte[] data, int start) {
    return parseWave(1, 25, 7, data, start);
  }

  /**
   * 解析波形
   *
   * @param group   分组(有几组)
   * @param waveLen 波形字节的长度
   * @param highLen 高位字节的长度
   * @param data    原始数据
   * @param start   开始的位置
   * @return 返回解析后的波形
   */
  public static int[] parseWave(int group, int waveLen, int highLen, byte[] data, int start) {
    int[] waves = new int[waveLen * group];
    for (int n = 0, i = 0; n < group; n++) {
      for (int j = 0; j < waveLen; j++) {
        waves[i++] = calculate(waveLen, highLen, data, start, n, j);
      }
    }
    return waves;
  }

  /**
   * 计算某一个波形值
   *
   * @param waveLen 波形长度
   * @param highLen 高位的长度
   * @param data    数据
   * @param start   开始的位置
   * @param group   当前处于第几组
   * @param index   下标
   * @return 返回计算的波形值
   */
  public static int calculate(int waveLen, int highLen, byte[] data, int start, int group, int index) {
    // 数据范围是“左开右闭”，以心电波形数据为例，其他同理
    // ============================================
    // 心电波形: (119, 371]  ==>: 252
    // ============================================
    // 心电波形1(高位)   (119, 132]   ==>: 13
    // 心电波形1(低位)   (132, 182]   ==>: 50
    // ============================================
    // 心电波形2(高位)   (182, 195]   ==>: 13
    // 心电波形2(低位)   (195, 245]   ==>: 50
    // ============================================
    // 心电波形3(高位)   (245, 258]   ==>: 13
    // 心电波形3(低位)   (258, 308]   ==>: 50
    // ============================================
    // 心电波形4(高位)   (308, 321]   ==>: 13
    // 心电波形4(低位)   (321, 371]   ==>: 50
    // ============================================
    // 4 * 63 ==>: 252
    // ============================================
    // 1个字节有8位
    // 每个波形的高位占2两位，如下标为119的字节值，8位分别为(132 ~ 136] 4个波形提供高位
    // ============================================
    // 以波形1为例：(119, 182]
    // 假如下标119的值为：55，即：‭0011 0111‬，第0个波形值取(0, 2]的2位(11)
    // 假如下标119的值为：55，即：‭0011 0111‬，第1个波形值取(2, 4]的2位(01)
    // 假如下标119的值为：55，即：‭0011 0111‬，第2个波形值取(4, 6]的2位(11)
    // 假如下标119的值为：55，即：‭0011 0111‬，第3个波形值取(6, 8]的2位(00)

    // 则，(((data[119] & (0000 0011)) >>> 0) << 8) | (data[132] & 0xFF)
    // 则，(((data[119] & (0000 1100)) >>> 2) << 8) | (data[133] & 0xFF)
    // 则，(((data[119] & (0011 0000)) >>> 4) << 8) | (data[134] & 0xFF)
    // 则，(((data[119] & (1100 0000)) >>> 6) << 8) | (data[135] & 0xFF)

    // 共有4组，每组占63个字节，n记录是第几组，假如第0组的第9个值，
    // 即，
    // 高位为：data[(n * 63) + (i / 4) + start]  ==>: data[(0 * 63) + (8 / 4) + 119] = data[121]
    // (data[121] & WAVE_BIT[i % 4]) >>> MOVE[i % 4]
    //
    // 低位为：data[(n * 63) + 13 + start]  ==>: data[(0 * 63) + 13 + 119] = data[132]
    // (data[132] & 0xFF)

    // 高位 | 低位  ==> 波形值

    int len = waveLen + highLen;
    int high = ((((data[group * len + (index / 4) + start] & 0xFF) & WAVE_BIT[index % 4]) >>> MOVE[index % 4]) << 8);
    int low = (data[group * len + highLen + index + start] & 0xFF);
    return (high | low);
  }

}
