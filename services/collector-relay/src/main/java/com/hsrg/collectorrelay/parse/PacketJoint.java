package com.hsrg.collectorrelay.parse;

import java.util.*;
import java.util.function.Function;

/**
 * 采集器拼包器
 */
public class PacketJoint {

  private final Function<Integer, CombinePacket> cpCreator = sn -> {
    CombinePacket cp = new CombinePacket(sn);
    cp.setTimeout(getTimeout());
    return cp;
  };
  /**
   * 存储数据
   */
  private final Map<Integer, CombinePacket> queue;
  /**
   * 最新的一个数据包
   */
  private int lastSn = -1;
  /**
   * 超时时间
   */
  private long timeout = 800;

  public PacketJoint() {
    this.queue = Collections.synchronizedMap(new TreeMap<>(Comparator.comparingInt(o -> o)));
  }

  public PacketJoint(long timeout) {
    this();
    this.timeout = timeout;
  }

  private int getPacketSn(byte[] data) {
    return CollectorHelper.getPacketSn(data);
  }

  public Map<Integer, CombinePacket> getQueue() {
    return queue;
  }

  public int getLastSn() {
    return lastSn;
  }

  public void setLastSn(int lastSn) {
    this.lastSn = lastSn;
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /**
   * 添加数据包
   *
   * @param data 数据
   * @return 返回数据包条数
   */
  public boolean add(byte[] data) {
    synchronized (this) {
      CombinePacket cp = getQueue().computeIfAbsent(getPacketSn(data), cpCreator);
      setLastSn(cp.getPacketSn());
      return cp.add(data, now());
    }
  }

  /**
   * 弹出一个组合后的数据
   */
  public List<CombinePacket> pop() {
    final Map<Integer, CombinePacket> q = this.getQueue();
    if (q.isEmpty()) {
      return Collections.emptyList();
    }
    long now = now();
    synchronized (this) {
      final List<CombinePacket> removes = new ArrayList<>(Math.min(2, q.size()));
      try {
        for (Integer sn : q.keySet()) {
          CombinePacket cp = q.get(sn);
          if (!filter(cp, now)) {
            removes.add(cp);
            cp.setDiscard(determinedDiscard(cp));
          }
        }
        return removes;
      } finally {
        if (!removes.isEmpty()) {
          removes.forEach(cp -> queue.remove(cp.getPacketSn()));
        }
      }
    }
  }

  /**
   * 过滤
   *
   * @param cp  数据包
   * @param now 当前时间
   * @return 返回是否过滤
   */
  private boolean filter(CombinePacket cp, long now) {
    if (now - cp.getRcvTime() >= getTimeout()) {
      return false;
    }
    // 数据包少于4个，且最后接收的时间少于1秒
    FlowmeterType type = cp.getFlowmeterType();
    if (type == FlowmeterType.UNKNOWN) {
      return true;
    }
    int size = cp.getPackets().size();
    return type.isFlag() ? size < 4 : size < 3;
  }

  /**
   * 是否拦截
   *
   * @param cp 数据包
   * @return 是否拦截
   */
  public static boolean determinedDiscard(CombinePacket cp) {
    FlowmeterType type = cp.getFlowmeterType();
    if (type == FlowmeterType.UNKNOWN) {
      return true;
    }
    if (!cp.getPackets().containsKey(0)) {
      return true;
    }
    if (PacketType.isRealtime(cp.getPacketType())) {
      // 普通数据3个包, [0, 1, 2]
      // 流速仪4个包, [0, 1, 2, 3]
      int size = cp.getPackets().size();
      return type.isFlag() ? size < 4 : size < 3;
    }
    return cp.getPackets().size() < 3;
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
