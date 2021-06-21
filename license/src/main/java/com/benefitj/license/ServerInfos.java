package com.benefitj.license;

import com.benefitj.core.cmd.CmdCall;
import com.benefitj.core.cmd.CmdExecutor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用于获取客户服务器的基本信息，如：IP、Mac地址、CPU序列号、主板序列号等
 */
public interface ServerInfos {

  final CmdExecutor EXECUTOR = new CmdExecutor();

  /**
   * 组装需要额外校验的License参数
   */
  default HardwareInfo getServerInfos() {
    try {
      HardwareInfo model = new HardwareInfo();
      model.setIpAddress(this.getIpAddress());
      model.setMacAddress(this.getMacAddress());
      model.setCpuSerial(this.getCPUSerial());
      model.setMainBoardSerial(this.getMainBoardSerial());
      model.setSerialNumber(this.getSerialNumber());
      return model;
    } catch (Exception e) {
      throw new IllegalStateException("获取服务器硬件信息失败: " + e.getMessage());
    }
  }

  /**
   * 获取机器码
   */
  String getSerialNumber();

  /**
   * 获取CPU序列号
   */
  String getCPUSerial();

  /**
   * 获取主板序列号
   */
  String getMainBoardSerial();

  /**
   * 获取IP地址
   */
  default List<String> getIpAddress() {
    try {
      // 获取所有网络接口
      List<InetAddress> inetAddresses = getLocalAllInetAddress();
      if (inetAddresses != null && !inetAddresses.isEmpty()) {
        return inetAddresses.stream()
            .map(InetAddress::getHostAddress)
            .distinct()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
      }
      return Collections.emptyList();
    } catch (SocketException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 获取Mac地址
   */
  default List<String> getMacAddress() {
    try {
      // 1. 获取所有网络接口
      List<InetAddress> inetAddresses = getLocalAllInetAddress();
      if (inetAddresses != null && !inetAddresses.isEmpty()) {
        // 2. 获取所有网络接口的Mac地址
        return inetAddresses.stream()
            .map(this::getMacByInetAddress)
            .distinct()
            .collect(Collectors.toList());
      }
      return Collections.emptyList();
    } catch (SocketException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 获取当前服务器所有符合条件的InetAddress
   */
  default List<InetAddress> getLocalAllInetAddress() throws SocketException {
    List<InetAddress> result = new ArrayList<>(4);
    // 遍历所有的网络接口
    for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
      NetworkInterface iface = networkInterfaces.nextElement();
      // 在所有的接口下再遍历IP
      for (Enumeration<InetAddress> inetAddresses = iface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
        InetAddress inetAddr = inetAddresses.nextElement();
        //排除LoopbackAddress、SiteLocalAddress、LinkLocalAddress、MulticastAddress类型的IP地址
        if (!inetAddr.isLoopbackAddress() /*&& !inetAddr.isSiteLocalAddress()*/
            && !inetAddr.isLinkLocalAddress() && !inetAddr.isMulticastAddress()) {
          result.add(inetAddr);
        }
      }
    }
    return result;
  }

  /**
   * 获取某个网络接口的Mac地址
   */
  default String getMacByInetAddress(InetAddress inetAddr) {
    try {
      byte[] mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < mac.length; i++) {
        if (i != 0) {
          sb.append("-");
        }
        //将十六进制byte转化为字符串
        String tmp = Integer.toHexString(mac[i] & 0xff);
        sb.append(tmp.length() == 1 ? "0" : "").append(tmp);
      }
      return sb.toString().toUpperCase();
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return null;
  }

  default String execObtainSerial(String cmd) {
    CmdCall call = EXECUTOR.call(cmd);
    if (call.isSuccessful()) {
      List<String> split = Stream.of(call.getMessage().split(CmdExecutor.CRLF))
          .map(String::trim)
          .filter(str -> !str.isEmpty())
          .collect(Collectors.toList());
      return split.get(split.size() - 1);
    }
    return "";
  }

}
