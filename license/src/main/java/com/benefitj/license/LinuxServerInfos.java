package com.benefitj.license;

/**
 * 用于获取客户Linux服务器的基本信息
 */
public class LinuxServerInfos implements ServerInfos {

  @Override
  public String getSerialNumber() {
    // 使用dmidecode命令获取CPU序列号
    return execObtainSerial("cat /sys/class/dmi/id/product_uuid");
  }

  @Override
  public String getCPUSerial() {
    // 使用dmidecode命令获取CPU序列号
    return execObtainSerial("dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1");
  }

  @Override
  public String getMainBoardSerial() {
    // 使用dmidecode命令获取主板序列号
    return execObtainSerial("dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1");
  }
}
