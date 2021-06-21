package com.benefitj.license;

/**
 * 用于获取客户Windows服务器的基本信息
 */
public class WindowsServerInfos implements ServerInfos {

  @Override
  public String getSerialNumber() {
    // 使用dmidecode命令获取CPU序列号
    return execObtainSerial("wmic bios get serialnumber");
  }

  @Override
  public String getCPUSerial() {
    // 使用WMIC获取CPU序列号
    return execObtainSerial("wmic cpu get processorid");
  }

  @Override
  public String getMainBoardSerial() {
    // 使用WMIC获取主板序列号
    return execObtainSerial("wmic baseboard get serialnumber");
  }
}
