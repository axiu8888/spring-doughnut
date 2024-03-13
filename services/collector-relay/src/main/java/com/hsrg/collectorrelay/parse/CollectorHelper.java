package com.hsrg.collectorrelay.parse;

import com.benefitj.core.HexUtils;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 采集器UDP包工具
 *
 * <p>包头：2字节，0x55, 0xAA
 * <p>长度：2字节，高位，低位
 * <p>设备ID：4字节
 * <p>包类型：1字节
 * <p>有效载荷：9 ~ n
 * <p>校验和：n + 1，1字节
 */
public class CollectorHelper {

  private static final Map<Integer, String> DEVICE_ID_CACHE = new WeakHashMap<>();
  private static final Function<Integer, String> SAVE_FUNC =
      deviceCode -> HexUtils.bytesToHex(HexUtils.intToBytes(deviceCode), true);
  private static final Map<Integer, byte[]> DEVICE_ID_BYTES_CACHE = new WeakHashMap<>();
  private static final Function<Integer, byte[]> SAVE_BYTES_FUNC = HexUtils::intToBytes;

  /**
   * 包头
   */
  public static final byte[] HEAD = new byte[]{0x55, (byte) 0xAA};
  /**
   * 数据类型的位置
   */
  public static final int TYPE = 8;

  /**
   * 是否为包头
   *
   * @param data 数据
   * @return 返回校验的结果，如果是返回true，否则返回false
   */
  public static boolean isHead(byte[] data) {
    return isHead(data, 0);
  }

  /**
   * 是否为包头
   *
   * @param data  数据
   * @param start 开始位置
   * @return 返回校验的结果，如果是返回true，否则返回false
   */
  public static boolean isHead(byte[] data, int start) {
    return (data[start] == HEAD[0]) && (data[start + 1] == HEAD[1]);
  }

  /**
   * 获取数据长度，数据的长度除去包头
   *
   * @param data 数据
   * @return 返回数据长度
   */
  public static int length(byte[] data) {
    return length(data, 2);
  }

  /**
   * 获取数据长度，数据的长度除去包头
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回数据的长度
   */
  public static int length(byte[] data, int start) {
    return HexUtils.bytesToInt(data[start], data[start + 1]);
  }

  /**
   * 是否匹配数据的长度，不包含包头的2字节
   *
   * @param data 数据
   * @return 返回是否匹配
   */
  public static boolean isLength(byte[] data) {
    return isLength(data, true);
  }

  /**
   * 是否匹配数据的长度，不包含包头的2字节
   *
   * @param data   数据
   * @param strict 是否严格模式
   * @return 返回是否匹配
   */
  public static boolean isLength(byte[] data, boolean strict) {
    int len = length(data);
    return strict ? len == (data.length - 2) : len <= (data.length - 2);
  }

  /**
   * 计算校验和，除最后一位外，所有字节顺序累加的结果
   *
   * @param data  数据
   * @param start 开始的位置
   * @param len   长度
   * @return 返回计算的校验和
   */
  public static byte checkSum(byte[] data, int start, int len) {
    byte sum = 0;
    for (int i = start, end = len - 1; i < end; i++) {
      sum += data[i];
    }
    return sum;
  }

  /**
   * 设置校验和
   *
   * @param data 数据
   * @return 返回设置校验和的数据
   */
  public static byte[] setCheckSum(byte[] data) {
    data[data.length - 1] = checkSum(data, 0, data.length);
    return data;
  }

  /**
   * 验证校验和
   *
   * @param data 数据
   * @return 返回校验和是否正确
   */
  public static boolean isCheckSum(byte[] data) {
    return isCheckSum(data, 0, data.length);
  }

  /**
   * 验证校验和
   *
   * @param data 数据
   * @param len  长度
   * @return 返回校验和是否正确
   */
  public static boolean isCheckSum(byte[] data, int start, int len) {
    return data.length >= (start + len) && checkSum(data, start, len) == data[start + len - 1];
  }

  /**
   * 校验数据
   *
   * @param data 数据
   * @return 返回数据是否正确
   */
  public static boolean verify(byte[] data) {
    return verify(data, true);
  }

  /**
   * 校验数据
   *
   * @param data   数据
   * @param strict 是否严格模式
   * @return 返回数据是否正确
   */
  public static boolean verify(byte[] data, boolean strict) {
    // 包头 && 长度 && 校验和
    return isHead(data) && isLength(data, strict)
        // 如果为严格模式，就整个字节数组判断，否则只要部分字节的数据匹配即可
        && (strict ? isCheckSum(data) : isCheckSum(data, 0, length(data) + 2));
  }

  /**
   * 获取数据类型
   *
   * @param data 数据
   * @return 返回数据类型
   */
  public static int getType(byte[] data) {
    return getType(data, TYPE);
  }

  /**
   * 获取数据类型
   *
   * @param data 数据
   * @return 返回数据类型
   */
  public static int getType(byte[] data, int start) {
    return data[start] & 0xFF;
  }

  /**
   * 判断是否为某个类型
   *
   * @param data 数据
   * @param type 判断的类型
   * @return 返回是否匹配
   */
  public static boolean isType(byte[] data, byte type) {
    return getType(data) == (type & 0xFF);
  }

  /**
   * 获取数据类型
   *
   * @param data 数据
   * @return 返回数据类型
   */
  public static com.hsrg.collectorrelay.parse.PacketType getPacketType(byte[] data) {
    return com.hsrg.collectorrelay.parse.PacketType.valueOf(getType(data));
  }

  /**
   * 是否是数据包
   *
   * @param raw 数据
   * @return 返回是否为数据包
   */
  public static boolean isData(byte[] raw) {
    return isHead(raw) && com.hsrg.collectorrelay.parse.PacketType.isData(getType(raw));
  }

  /**
   * 获取设备ID，小写的16进制
   *
   * @param data 数据
   * @return 返回16进制的设备ID
   */
  public static String getHexDeviceId(byte[] data) {
    //return getHexDeviceId(data, FLAG_DEVICE, true);
    if (isHead(data)) {
      return DEVICE_ID_CACHE.computeIfAbsent(HexUtils.bytesToInt(data, 4, 4), SAVE_FUNC);
    }
    return "";
  }

  /**
   * 获取设备ID
   *
   * @param data        数据
   * @param start       开始的位置
   * @param isLowerCase 是否小写
   * @return 返回16进制的设备ID
   */
  public static String getHexDeviceId(byte[] data, int start, boolean isLowerCase) {
    byte[] deviceId = getDeviceId(data, start);
    return HexUtils.bytesToHex(deviceId, isLowerCase);
  }

  /**
   * 获取整形的设备ID
   *
   * @param data 数据
   * @return 返回整形设备ID
   */
  public static int getDeviceCode(byte[] data) {
    byte[] deviceId = getDeviceId(data);
    return HexUtils.bytesToInt(deviceId);
  }

  /**
   * 获取整形的设备ID
   *
   * @param data  数据
   * @param start 开始位置
   * @return 返回整形设备ID
   */
  public static int getDeviceCode(byte[] data, int start) {
    byte[] deviceId = getDeviceId(data, start);
    return HexUtils.bytesToInt(deviceId);
  }

  /**
   * 获取设备ID的字节数组
   *
   * @param data 数据
   * @return 返回设备ID的字节数组
   */
  public static byte[] getDeviceId(byte[] data) {
    return getDeviceId(data, 4);
  }

  /**
   * 获取设备ID的字节数组
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回设备ID的字节数组
   */
  public static byte[] getDeviceId(byte[] data, int start) {
    // 4 ~ 7
    byte[] deviceId = new byte[4];
    System.arraycopy(data, start, deviceId, 0, deviceId.length);
    return deviceId;
  }

  /**
   * 获取设备ID的字节数组
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回设备ID的字节数组
   */
  public static byte[] getCachedDeviceId(byte[] data, int start) {
    return DEVICE_ID_BYTES_CACHE.computeIfAbsent(HexUtils.bytesToInt(data, start, 4), SAVE_BYTES_FUNC);
  }

  /**
   * 设备型号，2个字节
   *
   * @param data 数据
   * @return 获取设备型号
   */
  public static int getEquipmentType(byte[] data) {
    return getEquipmentType(data, 9);
  }

  /**
   * 设备型号，2个字节
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 获取设备型号
   */
  public static int getEquipmentType(byte[] data, int start) {
    return HexUtils.bytesToInt(data[start], data[start + 1]);
  }

  /**
   * 获取硬件版本
   *
   * @param data 数据
   * @return 返回版本
   */
  public static String getHardwareVersion(byte[] data) {
    byte b = data[11];
    return (b & 0b11100000) + "." + (b & 0b00011100) + "." + (b & 0b00000011);
  }

  /**
   * 获取软件版本
   *
   * @param data 数据
   * @return 返回版本
   */
  public static String getSoftwareVersion(byte[] data) {
    byte b = data[12];
    return (b & 0b11100000) + "." + (b & 0b00011100) + "." + (b & 0b00000011);
  }

  /**
   * 获取包序号，4个字节
   *
   * @param data 数据
   * @return 返回包序号
   */
  public static int getPacketSn(byte[] data) {
    return getPacketSn(data, 9);
  }

  /**
   * 获取包序号，4个字节
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回包序号
   */
  public static int getPacketSn(byte[] data, int start) {
    return HexUtils.bytesToInt(data[start], data[start + 1], data[start + 2], data[start + 3]);
  }

  /**
   * 生成字节数组
   *
   * @param deviceId 设备ID
   * @param type     类型
   * @param len      长度
   * @return 返回生成的字节数组
   */
  public static byte[] generate(String deviceId, byte type, int len) {
    byte[] deviceIdBytes = HexUtils.hexToBytes(deviceId);
    return generate(deviceIdBytes, type, len);
  }

  /**
   * 生成字节数组
   *
   * @param deviceId 设备ID
   * @param type     类型
   * @param len      长度
   * @return 返回生成的字节数组
   */
  public static byte[] generate(byte[] deviceId, byte type, int len) {
    byte[] data = new byte[len];
    data[0] = (byte) 0x55;
    data[1] = (byte) 0xAA;
    data[2] = (byte) ((len - 2) >> 8);
    data[3] = (byte) (len - 2);
    // 4 ~ 7
    System.arraycopy(deviceId, 0, data, 4, 4);
    data[8] = type;
    return data;
  }

  /**
   * 生成字节数组
   *
   * @param deviceId 设备ID
   * @param type     类型
   * @param raw      数据
   * @return 返回生成的字节数组
   */
  public static byte[] generate(byte[] deviceId, byte type, byte[] raw) {
    return generate(deviceId, type, raw, 0, raw.length);
  }

  /**
   * 生成字节数组
   *
   * @param deviceId  设备ID
   * @param type      类型
   * @param raw       数据
   * @param rawStart  数据开始的位置
   * @param rawLength 数据长度
   * @return 返回生成的字节数组
   */
  public static byte[] generate(byte[] deviceId, byte type, byte[] raw, int rawStart, int rawLength) {
    // 2(head) + 2(len) + 4(deviceId) + 1(type) + raw.length + 1(checkSum)
    byte[] data = generate(deviceId, type, 2 + 2 + 4 + 1 + raw.length + 1);
    System.arraycopy(raw, rawStart, data, 9, rawLength);
    return setCheckSum(data);
  }

  /**
   * 获取注册反馈，设备上线时，主动往服务端发送注册包，服务端需要给设备反馈
   *
   * @param data 数据
   * @return 返回反馈数据
   */
  public static byte[] getRegisterFeedback(byte[] data) {
    byte[] deviceId = getDeviceId(data);
    return getRegisterFeedback(deviceId, true);
  }

  /**
   * 获取注册反馈，设备上线时，主动往服务端发送注册包，服务端需要给设备反馈
   *
   * @param deviceId   设备ID
   * @param successful 是否成功
   * @return 返回反馈数据
   */
  public static byte[] getRegisterFeedback(byte[] deviceId, boolean successful) {
    return getRegisterFeedback(deviceId, nowS(), successful);
  }

  /**
   * 获取注册反馈，设备上线时，主动往服务端发送注册包，服务端需要给设备反馈
   *
   * @param deviceId   设备ID
   * @param time       时间(精确到秒)
   * @param successful 是否成功
   * @return 返回反馈数据
   */
  public static byte[] getRegisterFeedback(byte[] deviceId, long time, boolean successful) {
    // 反馈类型是 0x02
    byte[] feedback = generate(deviceId, (byte) 0x02, 15);
    feedback[9] = (byte) (successful ? 0xff : 0x00);
    // 设置时间
    setTime(feedback, time, 10);
    // 设置校验和，并返回数据
    return setCheckSum(feedback);
  }

  /**
   * 获取实时数据反馈数据包
   *
   * @param data 实时数据
   * @return 返回反馈字节数组
   */
  public static byte[] getRealtimeFeedback(byte[] data) {
    byte[] deviceId = getDeviceId(data);
    return getRealtimeFeedback(deviceId, (byte) 0x04, getPacketSn(data));
  }

  /**
   * 获取实时数据反馈数据包
   *
   * @param deviceId  设备ID
   * @param type      类型
   * @param packageSn 包号
   * @return 返回反馈字节数组
   */
  public static byte[] getRealtimeFeedback(byte[] deviceId, byte type, int packageSn) {
    return getRealtimeFeedback(deviceId, type, packageSn, true);
  }

  /**
   * 获取实时数据反馈数据包
   *
   * @param deviceId   设备ID
   * @param type       类型
   * @param packageSn  包号
   * @param successful 是否成功
   * @return 返回反馈字节数组
   */
  public static byte[] getRealtimeFeedback(
      byte[] deviceId, byte type, int packageSn, boolean successful) {
    byte[] feedback = generate(deviceId, type, 15);
    setPacketSn(feedback, packageSn, 9);
    feedback[13] = (byte) (successful ? 0x01 : 0x02);
    return setCheckSum(feedback);
  }

  /**
   * 获取血压数据反馈包
   *
   * @param raw 原数据
   * @return 返回反馈数据
   */
  public static byte[] getBloodPressureFeedback(byte[] raw) {
    byte[] feedback = new byte[14];
    // 0~12
    System.arraycopy(raw, 0, feedback, 0, 13);
    // 长度
    feedback[2] = (byte) 0x00;
    feedback[3] = (byte) 0x0C;
    // 校验和
    return CollectorHelper.setCheckSum(feedback);
  }

  /**
   * 获取删除日志文件的指令，服务端主动发指令删除设备中缓存的日志文件(CHE文件)
   *
   * @param deviceId 设备ID
   * @return 返回指令
   */
  public static byte[] getDeleteLogCmd(String deviceId) {
    // 包类型：0x0A
    byte[] cmd = generate(deviceId, (byte) 0x0A, 14);
    return setCheckSum(cmd);
  }

  /**
   * 是否成功删除日志，0xff 表示删除成功, 0x00 表示删除失败
   *
   * @param data 数据
   * @return 返回是否删除成功
   */
  public static boolean isDeleteLog(byte[] data) {
    return isSuccessful(data, (byte) 0x0A);
  }

  /**
   * 获取校准时间的指令
   *
   * @param deviceId 设备ID
   * @return 返回指令
   */
  public static byte[] getTimeCalibrationCmd(String deviceId) {
    // 包类型：0x09
    byte[] cmd = generate(deviceId, (byte) 0x09, 14);
    // 设置当前时间
    return copyTimeCalibrationCmd(cmd, nowS());
  }

  /**
   * 获取校准时间的指令
   *
   * @param cmd  时间校准指令
   * @param time 时间(精确到秒)
   * @return 返回指令
   */
  public static byte[] copyTimeCalibrationCmd(byte[] cmd, long time) {
    // 包类型：0x09
    // 设置当前时间
    setTime(cmd, time, 9);
    return setCheckSum(cmd);
  }

  /**
   * 获取注销设备的指令
   *
   * @param deviceId 设备ID
   * @return 返回指令
   */
  public static byte[] getUnregisterCmd(byte[] deviceId) {
    return getUnregisterCmd(deviceId, nowS());
  }

  /**
   * 获取注销设备的指令
   *
   * @param deviceId 设备ID
   * @param time     时间(秒)
   * @return 返回指令
   */
  public static byte[] getUnregisterCmd(byte[] deviceId, long time) {
    byte[] cmd = generate(deviceId, (byte) 0x0C, 14);
    setTime(cmd, time, 9);
    return setCheckSum(cmd);
  }

  /**
   * 是否注销成功
   *
   * @param data 数据
   * @return 返回是否注销
   */
  public static boolean isUnregisterSuccessful(byte[] data) {
    return isSuccessful(data, (byte) 0x0C);
  }

  /**
   * 获取开关指令
   *
   * @param deviceId 设备ID
   * @param status   开关状态，具体值请参考 {@link com.hsrg.collectorrelay.parse.SwitchType}
   * @return 返回设置开关状态的指令
   */
  public static byte[] getSwitchCmd(String deviceId, byte status) {
    byte[] cmd = generate(deviceId, (byte) 0x0B, 14);
    cmd[9] = status;
    return setCheckSum(cmd);
  }

  /**
   * 开关是否改变
   *
   * @param data 数据
   * @return 返回是否改变
   */
  public static boolean isSwitchChanged(byte[] data) {
    return isSuccessful(data, (byte) 0x0B);
  }

  /**
   * 下发或获取蓝牙外设的MAC地址
   * <p>
   * 操作类型：0x01:下发； 0x02:查询
   * <p>
   * 设备类型： 0x00:体温计；0x01:血氧仪； 0x02:血压计； 0x03:流速仪
   *
   * @param deviceId    设备ID
   * @param mac         蓝牙外设的MAC
   * @param operateType 操作类型
   * @param deviceType  外设类型
   * @return 返回设置指令
   */
  public static byte[] getBluetoothMacCmd(String deviceId, String mac, byte operateType, byte deviceType) {
    byte[] macBytes;
    if (operateType == 0x01) {
      if (mac == null || mac.trim().isEmpty()) {
        throw new IllegalArgumentException("mac");
      }
      String address = mac.contains(":") ? mac.replaceAll(":", "") : mac;
      macBytes = HexUtils.hexToBytes(address);
    } else {
      macBytes = null;
    }
    return getBluetoothMacCmd(deviceId, macBytes, operateType, deviceType);
  }

  /**
   * 下发或获取蓝牙外设的MAC地址
   * <p>
   * 操作类型：0x01:下发； 0x02:查询
   * <p>
   * 设备类型： 0x00:体温计；0x01:血氧仪； 0x02:血压计； 0x03:流速仪
   *
   * @param deviceId    设备ID
   * @param mac         蓝牙外设的MAC
   * @param operateType 操作类型
   * @param deviceType  外设类型
   * @return 返回设置指令
   */
  public static byte[] getBluetoothMacCmd(String deviceId, byte[] mac, byte operateType, byte deviceType) {
    if ((operateType != 0x01) && (operateType != 0x02)) {
      throw new IllegalArgumentException("不支持的操作类型");
    }
    if ((deviceType < 0x00) || (deviceType > 0x03)) {
      throw new IllegalArgumentException("不支持的设备类型");
    }
    byte[] cmd = generate(deviceId, (byte) 0x0D, 18);
    cmd[9] = operateType;
    cmd[10] = deviceType;
    if (operateType == 0x01 && mac != null) {
      System.arraycopy(mac, 0, cmd, 11, mac.length);
    }
    return setCheckSum(cmd);
  }

  /**
   * 获取重传包指令
   *
   * @param deviceId 设备ID
   * @param packetSn 包序号
   * @param len      重传长度
   * @return 返回重传指令
   */
  public static byte[] getRetryCmd(String deviceId, int packetSn, int len) {
    return getRetryCmd(HexUtils.hexToBytes(deviceId), packetSn, len);
  }

  /**
   * 获取重传包指令
   *
   * @param deviceId 设备ID
   * @param packetSn 包序号
   * @param len      重传长度(最多10个包)
   * @return 返回重传指令
   */
  public static byte[] getRetryCmd(byte[] deviceId, int packetSn, int len) {
    byte[] cmd = generate(deviceId, (byte) 0x08, 15);
    cmd[9] = (byte) Math.min(10, Math.max(1, len));
    setPacketSn(cmd, packetSn, 10);
    return setCheckSum(cmd);
  }

  /**
   * 获取集中上传通用数据包指令
   *
   * @param deviceId 设备ID
   * @param first    第一个包的序号
   * @param last     最后一个包的序号
   * @return 返回上传通用数据包指令
   */
  public static byte[] getFastUploadCmd(String deviceId, int first, int last) {
    return getFastUploadCmd(HexUtils.hexToBytes(deviceId), first, last);
  }

  /**
   * 获取集中上传通用数据包指令
   *
   * @param deviceId 设备ID
   * @param first    第一个包的序号
   * @param last     最后一个包的序号
   * @return 返回上传通用数据包指令
   */
  public static byte[] getFastUploadCmd(byte[] deviceId, int first, int last) {
    byte[] cmd = generate(deviceId, (byte) 0x10, 18);
    setPacketSn(cmd, first, 9);
    setPacketSn(cmd, last, 13);
    return setCheckSum(cmd);
  }

  /**
   * 设置包序号
   *
   * @param buff     字节数据
   * @param packetSn 包序号
   * @param start    开始的位置
   * @return 返回设置后的数据
   */
  private static byte[] setPacketSn(byte[] buff, int packetSn, int start) {
    buff[start] = (byte) ((packetSn >> 24) & 0xFF);
    buff[start + 1] = (byte) ((packetSn >> 16) & 0xFF);
    buff[start + 2] = (byte) ((packetSn >> 8) & 0xFF);
    buff[start + 3] = (byte) ((packetSn) & 0xff);
    return buff;
  }

  /**
   * 设置时间
   *
   * @param data  数据
   * @param time  时间（精确到秒）
   * @param start 开始的位置
   * @return 返回设置时间后的数据
   */
  public static byte[] setTime(byte[] data, long time, int start) {
    data[start] = (byte) ((time >> 24) & 0xFF);
    data[start + 1] = (byte) ((time >> 16) & 0xFF);
    data[start + 2] = (byte) ((time >> 8) & 0xFF);
    data[start + 3] = (byte) ((time) & 0xFF);
    return data;
  }

  /**
   * 获取时间戳
   *
   * @param data  数据
   * @param start 开始的位置
   * @param len   长度
   * @return 返回时间戳，精确到毫秒
   */
  public static long getTime(byte[] data, int start, int len) {
    long date = HexUtils.bytesToLong(data[start], data[start + 1], data[start + 2], data[start + 3]) * 1000;
    if (len <= 4) {
      return date;
    }
    return date + HexUtils.bytesToLong(data[start + 4], data[start + 5]);
  }

  /**
   * @return 返回当前时间，精确到秒
   */
  private static long nowS() {
    return System.currentTimeMillis() / 1000;
  }

  private static boolean isSuccessful(byte[] data, byte flag) {
    return (data[TYPE] == flag) && ((data[9] & 0xFF) == 0xFF);
  }

}
