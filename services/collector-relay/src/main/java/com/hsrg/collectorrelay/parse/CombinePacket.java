package com.hsrg.collectorrelay.parse;


import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 组合包
 */
public class CombinePacket {
  /**
   * 数据
   */
  private final Map<Integer, byte[]> packets;
  /**
   * 包序号
   */
  private int packetSn;
  /**
   * 数据包的时间
   */
  private long time = 0;
  /**
   * 数据包类型
   */
  private PacketType packetType;
  /**
   * 包类型
   */
  private FlowmeterType flowmeterType = FlowmeterType.UNKNOWN;
  /**
   * 接收时间
   */
  private long rcvTime;
  /**
   * 是否已丢弃
   */
  private boolean discard = true;
  /**
   * 超时时长
   */
  private long timeout = 800;

  public CombinePacket(int packetSn) {
    this.packetSn = packetSn;
    this.packets = Collections.synchronizedMap(new TreeMap<>(Integer::compareTo));
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public long getTimeout() {
    return timeout;
  }

  public boolean add(byte[] data, long now) {
    if (getPacketType() == null) {
      setPacketType(CollectorHelper.getPacketType(data));
    }
    long rcvTime = getRcvTime();
    getPackets().put((int) data[13], data);
    setRcvTime(now);
    if (data[13] == 0) {
      // 设置时间
      setTime(CollectorHelper.getTime(data, 14, 4));
    }
    // 设置包类型
    else if (data[13] == 2) {
      checkAndGetFlowmeterType(data);
    }
    if (!PacketType.isRealtime(getPacketType())) {
      return getPackets().size() >= 3;
    }
    if (getFlowmeterType() == FlowmeterType.No) {
      return getPackets().size() >= 3;
    }
    // 判断是否
    return getPackets().size() >= 4 || (now - rcvTime >= getTimeout());
  }

  public Map<Integer, byte[]> getPackets() {
    return packets;
  }

  public int getPacketSn() {
    return packetSn;
  }

  public void setPacketSn(int packetSn) {
    this.packetSn = packetSn;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public PacketType getPacketType() {
    return packetType;
  }

  public void setPacketType(PacketType packetType) {
    this.packetType = packetType;
  }

  public FlowmeterType getFlowmeterType() {
    return flowmeterType;
  }

  public void setFlowmeterType(FlowmeterType flowmeterType) {
    this.flowmeterType = flowmeterType;
  }

  public long getRcvTime() {
    return rcvTime;
  }

  public void setRcvTime(long rcvTime) {
    this.rcvTime = rcvTime;
  }

  public boolean isDiscard() {
    return discard;
  }

  public void setDiscard(boolean discard) {
    this.discard = discard;
  }

  public FlowmeterType checkAndGetFlowmeterType() {
    return checkAndGetFlowmeterType(getPackets().get(2));
  }

  public FlowmeterType checkAndGetFlowmeterType(byte[] segment3) {
    if (segment3 != null) {
      this.flowmeterType = ((segment3[172] & 0b10000000) >> 7) == 1 ? FlowmeterType.Yes : FlowmeterType.No;
    }
    return this.flowmeterType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CombinePacket that = (CombinePacket) o;
    return packetSn == that.packetSn;
  }

  @Override
  public int hashCode() {
    return Objects.hash(packetSn);
  }

  /**
   * 拼接数据包
   *
   * @return 返回拼接的数据包
   */
  public byte[] joint() {
    return isDiscard() ? null : joint(this);
  }

  /**
   * 拼接数据包
   *
   * @param cp 数据包
   * @return 返回拼接的数据包
   */
  public static byte[] joint(CombinePacket cp) {
    Map<Integer, byte[]> packets = cp.getPackets();
    byte[] segment1 = packets.get(0);
    byte[] segment2 = packets.get(1);
    byte[] segment3 = packets.get(2);
    byte[] segment4 = packets.get(3);

    boolean realtime = PacketType.isRealtime(cp.getPacketType());
    byte[] packet = new byte[realtime && segment4 != null ? 670 : 545];
    // 包头(2) + 长度(2) + 设备ID(4) + 包类型(1) + 包序号(4) ==>: 13
    System.arraycopy(segment1, 0, packet, 0, 13);// total(0, 13]
    // 设置类型
    if (realtime) {
      packet[8] = (byte) (segment4 != null ? 0xF3 : 0x03);
    }
    // 时间(4), (14, 19)    p(13, 18)
    System.arraycopy(segment1, 14, packet, 13, 6); // 14 + 6 = 20
    // 数据, segment1(19, 69)(呼吸波型)   p(19, 118)
    System.arraycopy(segment1, 20, packet, 19, 50); // 20 + 50 = 70
    // 数据, segment1(70, 195)    p(119, 245)
    System.arraycopy(segment1, 70, packet, 119, 126); // 119 + 126 = 245
    // 数据, segment2(14, 139)
    System.arraycopy(segment2, 14, packet, 245, 140 - 14); // 245 + 125 = 370
    // 数据, segment1(140, 189)(腹呼吸波型)   p(69, 118) segment1.length =>: 197
    System.arraycopy(segment2, 140, packet, 69, 50); // 140 + 50 = 190
    // 空出一个字节
    // 370
    // 数据, segment3(14, 187)
    System.arraycopy(segment3, 14, packet, 371, 187 - 14); // 371 + 173 = 544

    // 拷贝流速仪数据
    if (realtime && segment4 != null) {
      // 数据, segment4(14, 139)
      System.arraycopy(segment4, 14, packet, 544, 139 - 14); // 544 + 125 = 669
    }
    // 设置校验和(545/670)
    return CollectorHelper.setCheckSum(packet);
  }

}
