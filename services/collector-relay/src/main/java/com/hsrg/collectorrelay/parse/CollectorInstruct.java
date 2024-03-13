package com.hsrg.collectorrelay.parse;


import com.benefitj.core.HexUtils;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 采集器指令
 */
public interface CollectorInstruct {

  enum Cache {
    INSTANCE;

    private final Map<String, byte[]> hexDeviceIdCache = new WeakHashMap<>();

    private final Function<String, byte[]> func = HexUtils::hexToBytes;

    public byte[] getDeviceId(String deviceId) {
      return hexDeviceIdCache.computeIfAbsent(deviceId, func);
    }

  }

  /**
   * 申请字节数组
   *
   * @param size     数组大小
   * @param deviceId 设备ID
   * @return 返回字节数组
   */
  default byte[] alloc(String deviceId, byte type, int size) {
    byte[] data = new byte[size];
    data[0] = 0x55;
    data[1] = (byte) 0xAA;
    byte[] deviceIdBytes = Cache.INSTANCE.getDeviceId(deviceId);
    System.arraycopy(deviceIdBytes, 0, data, 4, deviceIdBytes.length);
    data[8] = type;
    return data;
  }

  /**
   * 设置校验和
   *
   * @param data 数据
   * @return 返回设置校验和后的数据
   */
  default byte[] checkSum(byte[] data) {
    return CollectorHelper.setCheckSum(data);
  }

  /**
   * 设置 RTC 日期/时间
   *
   * @param deviceId 设备ID
   * @param time     时间
   * @return 返回指令
   */
  default byte[] setRTCTime(String deviceId, long time) {
    byte[] cmd = alloc(deviceId, (byte) 0x09, 14);
    CollectorHelper.setTime(cmd, time, 9);
    return checkSum(cmd);
  }

}
