package com.hsrg.collectorrelay.parse;

import com.benefitj.core.*;
import com.hsrg.collectorrelay.parse.bean.HardwarePacket;
import com.hsrg.utils.entity.mongo.HardwarePackage;
import com.hsrg.utils.hardware.parse.ProcessManager;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * CHE工具类
 */
public class CheHelper {

  private static final ByteArrayCopy BUF_COPY = ByteArrayCopy.newBufCopy();
  private static final ThreadLocal<Long> TOTAL_COUNTER = ThreadLocal.withInitial(() -> 0L);

  public static final int PACKET_SIZE = 576;
  /**
   * 空设备ID
   */
  private static final byte[] EMPTY_DEVICE_ID = new byte[0];

  /**
   * 读取包头
   *
   * @param is 输入流
   * @return 返回包头信息
   */
  public static com.hsrg.collectorrelay.parse.CheFileHeader parseHeader(InputStream is) {
    try {
      byte[] buf = getCache(PACKET_SIZE);
      is.read(buf);
      return parseHeader(buf, false);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 读取包头
   *
   * @param cheFile CHE文件
   * @return 返回包头信息
   */
  public static com.hsrg.collectorrelay.parse.CheFileHeader parseHeader(File cheFile) {
    return parseHeader(cheFile, false);
  }

  /**
   * 读取包头
   *
   * @param cheFile CHE文件
   * @return 返回包头信息
   */
  public static com.hsrg.collectorrelay.parse.CheFileHeader parseHeader(File cheFile, boolean hasRaw) {
    try (final RandomAccessFile cheRaf = new RandomAccessFile(cheFile, "r");) {
      byte[] bytes = getCache(PACKET_SIZE);
      cheRaf.readFully(bytes);
      return parseHeader(bytes, hasRaw);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 解析第一个包和最后一个包，默认 2019年7月1日 之后的数据
   *
   * @param cheFile CHE文件
   * @return 返回解析的数据包
   */
  public static List<HardwarePackage> obtainFirstAndLast(File cheFile) {
    long time = TimeUtils.toTime(2019, 7, 1);
    return obtainFirstAndLast(cheFile, time);
  }

  /**
   * 获取第一个包和最后一个包
   *
   * @param cheFile   CHE文件
   * @param afterTime 某个时间之后的数据包
   * @return 返回解析的数据包
   */
  public static List<HardwarePackage> obtainFirstAndLast(File cheFile, long afterTime) {
    final long afterTimeSecond = afterTime / 1000;
    final List<HardwarePackage> packages = new ArrayList<>(2);

    // 先比较第一个包和最后一个包的时间，如果第一个包比最后一个包的时间大，
    // 说明数据包存在覆盖，查找到真正的第一个包，前一个包理论上是最后一个包
    try (final RandomAccessFile raf = new RandomAccessFile(cheFile, "r")) {
      HardwarePackage first = getPosition(raf, 576);
      HardwarePackage last = getPosition(raf, (cheFile.length() / 576 - 1) * 576);

      if (first.getTime() > last.getTime()) {
        long position = 576;
        HardwarePackage tmpFirst, tmpLast = first;
        for (; ; ) {
          tmpFirst = getPosition(raf, position);
          if (tmpFirst.getTime() < tmpLast.getTime()) {
            // 查找到数据包的开始和结束位置
            if (tmpFirst.getTime() < afterTimeSecond) {
              if (tmpLast.getTime() <= afterTimeSecond) {
                throw new IllegalStateException("此时间段内没有数据");
              }

              // 查找第一个比要求结束时间大的包
              long firstPosition = position;
              for (; ; ) {
                firstPosition += 576;
                if (firstPosition >= cheFile.length()) {
                  break;
                }
                tmpFirst = getPosition(raf, firstPosition);
                if (tmpFirst.getTime() >= afterTimeSecond) {
                  break;
                }
              }
            }
            packages.add(tmpFirst);
            packages.add(tmpLast);
            return packages;
          }
          tmpLast = tmpFirst;
          position += 576;
          if (position >= cheFile.length()) {
            break;
          }
        }
        return packages;
      } else {
        if (first.getTime() >= afterTimeSecond) {
          packages.add(first);
          packages.add(last);
        } else {
          long position = 576;
          for (; ; ) {
            first = getPosition(raf, position);
            if (first.getTime() >= afterTimeSecond) {
              break;
            }
            position += 576;
            if (position >= cheFile.length()) {
              break;
            }
          }
          packages.add(first);
          packages.add(last);
        }
        return packages;
      }
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 获取 2019年7月1日 之后的第一个包的时间
   *
   * @param cheFile CHE文件
   * @return 返回获取的时间，如果没有取到，返回-1
   */
  public static long getFirstTime(File cheFile) {
    long afterTime = TimeUtils.toTime(2019, 7, 1);
    return getFirstTime(cheFile, afterTime);
  }

  /**
   * 获取某个时间之后的第一个包的时间
   *
   * @param cheFile   CHE文件
   * @param afterTime 某个时间之后
   * @return 返回获取的时间，如果没有取到，返回-1
   */
  public static long getFirstTime(File cheFile, long afterTime) {
    List<HardwarePackage> packages = obtainFirstAndLast(cheFile, afterTime);
    return packages.get(0).getTime() * 1000;
  }

  /**
   * 获取最后一个包的时间
   *
   * @param cheFile CHE文件
   * @return 返回获取的时间，如果没有取到，返回-1
   */
  public static long getLastTime(File cheFile) {
    long afterTime = TimeUtils.toTime(2019, 7, 1);
    List<HardwarePackage> packages = obtainFirstAndLast(cheFile, afterTime);
    return packages.get(1).getTime() * 1000;
  }

  /**
   * 获取某个时间之后的最后一个包的时间
   *
   * @param cheFile  CHE文件
   * @param position 位置
   * @return 返回获取的时间，如果没有取到，返回-1
   */
  public static HardwarePackage getPosition(File cheFile, long position) {
    try (final RandomAccessFile raf = new RandomAccessFile(cheFile, "r")) {
      return getPosition(raf, position);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 获取某个位置的包
   *
   * @param raf      文件读取对象
   * @param position 位置
   * @return 返回解析的包
   */
  public static HardwarePackage getPosition(final RandomAccessFile raf, long position) throws IOException {
    byte[] buf = getCache(576);
    raf.seek(position);
    raf.read(buf);
    byte[] udp = convertToUdp(buf);
    return ProcessManager.parse2BasePackage(udp);
  }

  /**
   * 解析数据包
   *
   * @param cheFile     CHE文件
   * @param filter      过滤器，过滤不匹配的数据包
   * @param consumer    消费，处理数据包
   * @param interceptor 拦截器，如果满足条件，停止读取和解析
   */
  public static void parseCheFile(File cheFile,
                                  Predicate<HardwarePackage> filter,
                                  Consumer<HardwarePackage> consumer,
                                  Predicate<HardwarePackage> interceptor) {
    parseCheFile(cheFile, filter, (hbp, raf) -> consumer.accept(hbp), interceptor);
  }

  /**
   * 解析数据包
   *
   * @param cheFile     CHE文件
   * @param filter      过滤器，过滤不匹配的数据包
   * @param consumer    消费，处理数据包
   * @param interceptor 拦截器，如果满足条件，停止读取和解析
   */
  public static void parseCheFile(File cheFile,
                                  Predicate<HardwarePackage> filter,
                                  BiConsumer<HardwarePackage, RandomAccessFile> consumer,
                                  Predicate<HardwarePackage> interceptor) {
    try (final RandomAccessFile cheRaf = new RandomAccessFile(cheFile, "r");) {
      byte[] buf = getCache(PACKET_SIZE);
      cheRaf.read(buf);
      long total = 0;
      int len;
      while ((len = cheRaf.read(buf)) > 0) {
        total += len;
        TOTAL_COUNTER.set(total);
        byte[] udp = convertToUdp(buf);
        HardwarePackage hp = ProcessManager.parse2BasePackage(udp);
        hp.setRealTime(false);
        if (filter.test(hp)) {
          consumer.accept(hp, cheRaf);
        }
        if (interceptor.test(hp)) {
          break;
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    } finally {
      TOTAL_COUNTER.remove();
    }
  }

  /**
   * 获取读取的总长度
   */
  public static long getTotal() {
    return TOTAL_COUNTER.get();
  }

  /**
   * 转换成 HardwarePackage
   *
   * @param cheFile  CHE文件
   * @param consumer 处理
   */
  public static void parseHardwarePackage(File cheFile, Consumer<HardwarePackage> consumer) {
    try (final FileInputStream fis = new FileInputStream(cheFile);) {
      fis.read(getCache(PACKET_SIZE));
      parseCheFile(cheFile, hp -> true, (hp, raf) -> consumer.accept(hp), hp -> false);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换成 HardwarePackage
   *
   * @param cheFile  CHE文件
   * @param consumer 处理
   */
  public static void parseHardwarePacket(File cheFile, Consumer<HardwarePacket> consumer) {
    try (final FileInputStream fis = new FileInputStream(cheFile);) {
      fis.read(getCache(PACKET_SIZE));
      parseHardwarePacket(fis, consumer);
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换成 HardwarePacket
   *
   * @param in       数据流
   * @param consumer 处理
   */
  public static void parseHardwarePacket(InputStream in, Consumer<HardwarePacket> consumer) {
    try {
      byte[] buf = getCache(PACKET_SIZE);
      while (in.read(buf) > 0) {
        consumer.accept(com.hsrg.collectorrelay.parse.PacketUtils.parse(buf, new HardwarePacket(false)));
      }
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  /**
   * 获取缓存
   *
   * @param size 缓存大小
   * @return 返回缓存
   */
  public static byte[] getCache(int size) {
    return BUF_COPY.getCache(size);
  }

  /**
   * 拷贝数据
   *
   * @param data  数据
   * @param start 开始的位置
   * @param size  拷贝数据缓冲大小
   * @return 返回拷贝的数据
   */
  public static byte[] copy(byte[] data, int start, int size) {
    return copy(data, start, size, true);
  }

  /**
   * 拷贝数据
   *
   * @param data  数据
   * @param start 开始的位置
   * @param size  拷贝数据缓冲大小
   * @param local 是否使用本地缓存
   * @return 返回拷贝的数据
   */
  public static byte[] copy(byte[] data, int start, int size, boolean local) {
    byte[] buf = local ? getCache(size) : new byte[size];
    System.arraycopy(data, start, buf, 0, buf.length);
    return buf;
  }


  /**
   * 字节数组转换成长整数
   *
   * @param bytes 字节数组
   * @return 返回长整数值
   */
  public static long byteToLong(byte... bytes) {
    long value = 0;
    for (byte b : bytes) {
      value <<= 8;
      value |= b & 0xff;
    }
    return value;
  }

  /**
   * 字节转换成整型
   *
   * @param bytes 字节数组
   * @return 返回整型值
   */
  public static int byteToInt(byte... bytes) {
    return (int) byteToLong(bytes);
  }

  /**
   * 获取包序号
   *
   * @param data 数据
   * @return 返回包序号
   */
  public static int getPacketSn(byte[] data) {
    return getPacketSn(data, 0);
  }

  /**
   * 获取包序号
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回包序号
   */
  public static int getPacketSn(byte[] data, int start) {
    return byteToInt(copy(data, start, 4));
  }

  /**
   * 转换成UDP数据包
   *
   * @param raw 源数据
   * @return 返回UDP数据包
   */
  public static byte[] convertToUdp(byte[] raw) {
    return convertToUdp(-1, raw, 0);
  }

  /**
   * 转换成UDP数据包
   *
   * @param raw   源数据
   * @param start 开始的位置
   * @return 返回UDP数据包
   */
  public static byte[] convertToUdp(byte[] raw, int start) {
    return convertToUdp(-1, raw, start);
  }

  /**
   * 转换成UDP数据包
   *
   * @param time  时间
   * @param raw   源数据
   * @param start 开始的位置
   * @return 返回UDP数据包
   */
  public static byte[] convertToUdp(long time, byte[] raw, int start) {
    return convertToUdp(EMPTY_DEVICE_ID, -1, time, raw, start);
  }

  /**
   * 转换成UDP数据包
   *
   * @param deviceId 设备ID
   * @param raw      源数据
   * @param start    开始的位置
   * @return 返回UDP数据包
   */
  public static byte[] convertToUdp(byte[] deviceId, byte[] raw, int start) {
    return convertToUdp(deviceId, -1, -1, raw, start);
  }

  /**
   * 转换成UDP数据包
   *
   * @param deviceId 设备ID
   * @param packetSn 包序号
   * @param time     时间
   * @param raw      源数据
   * @param start    开始的位置
   * @return 返回UDP数据包
   */
  public static byte[] convertToUdp(byte[] deviceId, int packetSn, long time, byte[] raw, int start) {
    return convertToUdp(deviceId, packetSn, time, raw, start, true);
  }

  /**
   * 转换成UDP数据包
   *
   * @param deviceId  设备ID
   * @param packetSn  包序号
   * @param time      时间
   * @param raw       源数据
   * @param start     开始的位置
   * @param useBuffer 是否使用缓存
   * @return 返回UDP数据包
   */
  public static byte[] convertToUdp(byte[] deviceId, int packetSn, long time, byte[] raw, int start, boolean useBuffer) {
    byte[] data = useBuffer ? getCache(545) : new byte[545];
    // 包头
    data[0] = 0x55;
    data[1] = (byte) 0xAA;
    // 长度
    data[2] = 0x02;
    data[3] = 0x1F;

    // 设备ID
    if (deviceId != null && deviceId.length == 4) {
      // 4 ~ 7
      System.arraycopy(deviceId, 0, data, 4, deviceId.length);
    }

    // 类型
    data[8] = 0x03;

    // 拷贝数据，start: 9,
    // len: 2(head) + 2(length) + 4(deviceId) + 1(type) + 1(checkSum)
    System.arraycopy(raw, start, data, 9, data.length - 10);

    // 包序号
    if (packetSn > 0) {
      // start:
      byte[] buf = numberToBytes(packetSn, 32, true);
      System.arraycopy(buf, 0, data, 9, buf.length);
    }

    // 修改时间
    if (time > 0) {
      byte[] prefix = numberToBytes(time / 1000, 32, true);
      byte[] suffix = numberToBytes(time % 1000, 16, true);
      System.arraycopy(prefix, 0, data, 13, prefix.length);
      System.arraycopy(suffix, 0, data, 17, suffix.length);
    }

    // 校验和
    data[data.length - 1] = checkSum(data);

    return data;
  }

  /**
   * 整形数值转换成字节数组
   *
   * @param num   整形数值
   * @param bit   位，根据位取几个字节
   * @param local 是否使用本地缓存
   * @return 返回转换后的字节数组
   */
  public static byte[] numberToBytes(long num, int bit, boolean local) {
    int size = bit / 8;
    byte[] b = local ? getCache(size) : new byte[size];
    for (int i = 0; i < size; i++) {
      b[i] = (byte) (num >> ((bit - 8) - i * 8));
    }
    return b;
  }

  /**
   * 计算校验和，除最后一位外，所有字节顺序累加的结果
   *
   * @param data 数据
   * @return 返回计算的校验和
   */
  public static byte checkSum(byte[] data) {
    byte sum = 0;
    int end = data.length - 1;
    for (int i = 0; i < end; i++) {
      sum += data[i];
    }
    return sum;
  }

  /**
   * 转换CHE的时间
   *
   * @param srcFile   源文件
   * @param destFile  目标文件
   * @param startTime 开始时间(毫秒)
   */
  public static void changeCHETime(File srcFile, File destFile, long startTime) {
    if (!srcFile.exists() || srcFile.length() < PACKET_SIZE * 2) {
      throw new IllegalArgumentException("文件不存在或文件太小");
    }

    if (destFile == null) {
      com.hsrg.collectorrelay.parse.CheFileHeader info = parseHeader(srcFile);
      // 0000037E-2019_11_06-14_58_46.CHE
      String destFilename = String.format("%s-%s.CHE"
          , info != null ? info.getDeviceId() : srcFile.getName().split("-")[0]
          , DateFmtter.fmt(startTime, "yyyy_MM_dd-HH_mm_ss")
      );
      destFile = new File(srcFile.getParentFile(), destFilename);
    }

    checkAndCreate(destFile);
    try (final FileInputStream in = new FileInputStream(srcFile);
         final FileOutputStream out = new FileOutputStream(destFile);) {
      byte[] buf = new byte[PACKET_SIZE];
      int len, index = 0;
      long total = 0;
      while ((len = in.read(buf)) > 0) {
        total += len;
        if (total <= PACKET_SIZE) {
          com.hsrg.collectorrelay.parse.CheFileHeader info = parseHeader(buf, false);
          if (info != null) {
            out.write(buf, 0, len);
            out.flush();
            continue;
          }
        }

        // (4 ~ 9)
        byte[] time = numberToBytes(startTime / 1000 + index, 32, true);
        buf[4] = time[0];
        buf[5] = time[1];
        buf[6] = time[2];
        buf[7] = time[3];
        buf[8] = 0;
        buf[9] = 0;
        index++;

        out.write(buf, 0, len);
        out.flush();
      }
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  private static boolean checkAndCreate(File destFile) {
    if (!destFile.exists()) {
      try {
        destFile.getParentFile().mkdirs();
        return destFile.createNewFile();
      } catch (IOException e) {
        throw new IllegalStateException(CatchUtils.findRoot(e));
      }
    }
    return true;
  }

  /**
   * 解析CHE文件头
   *
   * @param headerInfo 文件头信息
   * @return 返回解析的信息
   */
  @Nullable
  public static com.hsrg.collectorrelay.parse.CheFileHeader parseHeader(byte[] headerInfo, boolean hasRaw) {
    if (headerInfo != null && headerInfo.length > 300) {
      String str = new String(headerInfo, StandardCharsets.UTF_8);
      com.hsrg.collectorrelay.parse.CheFileHeader info = new com.hsrg.collectorrelay.parse.CheFileHeader();
      info.setOriginal(str);
      if (hasRaw) info.setRaw(Arrays.copyOf(headerInfo, headerInfo.length));
      return parseHeader(str.trim(), info);
    }
    return null;
  }

  /**
   * 解析CHE文件头
   *
   * @param headerInfo 文件头信息
   * @return 返回解析的信息
   */
  @Nullable
  public static com.hsrg.collectorrelay.parse.CheFileHeader parseHeader(String headerInfo) {
    return parseHeader(headerInfo, new com.hsrg.collectorrelay.parse.CheFileHeader());
  }

  /**
   * 解析CHE文件头
   *
   * @param headerInfo 文件头信息
   * @param info       实体
   * @return 返回解析的信息
   */
  @Nullable
  public static com.hsrg.collectorrelay.parse.CheFileHeader parseHeader(String headerInfo, com.hsrg.collectorrelay.parse.CheFileHeader info) {
    if (!(headerInfo.contains("ID") && headerInfo.contains("FirmVer"))) {
      return null;
    }
    String[] splits = headerInfo.split("_");
    int startAt = splits[0].endsWith("SensEcho") || splits[0].endsWith("ensEcho") ? 0 : 1;
    for (int i = 0; i < splits.length - 1; i++) {
      String split = splits[startAt + i];
      switch (i) {
        case 0:
          // 产品名称: SensEcho
          info.setProductName(split);
          break;
        case 1:
          // 产品型号: 5A4.0
          info.setProductType(split);
          break;
        case 2:
          // 协议版本号: V1.0.0
          info.setVersion(split);
          break;
        case 3:
          // 固件版本号: V1.0.0
          info.setFirmwareVersion(split.split(":")[1]);
          break;
        case 4:
          // 硬件版本号: V1.0.0
          info.setHardwareVersion(split.split(":")[1]);
          break;
        case 5:
          // 设备型号 + ID
          info.setDeviceId(split.split(":")[1].toLowerCase());
          break;
        case 6:
          // 呼吸信息
          info.setRespInfo(split.split(":")[1]);
          break;
        case 7:
          // 心电信息
          info.setEcgInfo(split.split(":")[1]);
          break;
        case 8:
          // 三轴信息
          info.setXyzInfo(split.split(":")[1]);
          break;
        case 9:
          // 血氧信息
          info.setSpo2Info(split.split(":")[1]);
          break;
      }
    }
    return info;
  }
}
