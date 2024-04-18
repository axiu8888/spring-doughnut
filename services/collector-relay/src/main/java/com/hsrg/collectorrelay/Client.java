package com.hsrg.collectorrelay;

import com.benefitj.core.ByteArrayCopy;
import com.benefitj.core.EventLoop;
import com.benefitj.core.HexUtils;
import com.benefitj.core.TimeUtils;
import com.benefitj.core.file.FileCopy;
import com.benefitj.core.file.RaFile;
import com.benefitj.netty.client.TcpNettyClient;
import com.benefitj.netty.handler.ActiveHandler;
import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.listener.OnAppStop;
import com.hsrg.collectorrelay.parse.CheFileHeader;
import com.hsrg.collectorrelay.parse.CheHelper;
import com.hsrg.utils.entity.mongo.HardwarePackage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Component
@Slf4j
public class Client extends TcpNettyClient {

  @Autowired
  Options options;

  InetSocketAddress remote;
  List<CheFile> ches = new CopyOnWriteArrayList<>();
  EventLoop single = EventLoop.single();

  @OnAppStart
  public void onAppStart() {
    File cheDir = new File(options.cheDir);
    File[] files = cheDir.listFiles();
    if (files == null || files.length == 0) {
      log.info("缺少CHE文件，3秒后结束程序!");
      EventLoop.asyncIO(() -> System.exit(0), 3, TimeUnit.SECONDS);
      return;
    }
    ches.addAll(Stream.of(files)
        .filter(File::isFile)
        .filter(f -> f.getName().endsWith(".CHE"))
        .filter(f -> f.length() > 576 * 3)
        .filter(f -> {
          if (f.length() % 576.0 == 0.0) {
            return true;
          }
          log.info("错误的CHE文件, {}", f.getAbsolutePath());
          f.renameTo(new File(f.getParentFile(), f.getName() + "____delete"));
          return false;
        })
        .sorted(File::compareTo)
        .map(CheFile::new)
        .toList());

    if (ches.isEmpty()) {
      log.info("缺少CHE文件，3秒后结束程序!");
      EventLoop.asyncIO(() -> System.exit(0), 3, TimeUnit.SECONDS);
      return;
    }

    String[] ip_port = options.remote.split(":");
    this.remote = new InetSocketAddress(ip_port[0], Integer.parseInt(ip_port[1]));
    start(f -> {
      log.info("开启代理, local: {}, remote: {}", localAddress(), remoteAddress());
      if (!f.isSuccess()) {
        stop(ff -> System.exit(0)); // 终止
      }
    });
  }

  @OnAppStop
  public void OnAppStop() {
    stop(f -> log.info("停止代理, local: {}, remote: {}", localAddress(), remoteAddress()));
    // 结束进程
    EventLoop.asyncIO(() -> System.exit(0), 3, TimeUnit.SECONDS);
  }

  @Override
  protected TcpNettyClient useDefaultConfig() {
    //this.autoReconnect(true, 5, TimeUnit.SECONDS);
    this.remoteAddress(remote);
    this.handler(new ChannelInitializer<Channel>() {

      @Override
      protected void initChannel(Channel ch) throws Exception {
        onConnect(ch);
      }
    });
    return super.useDefaultConfig();
  }

  private void onConnect(Channel ch) {
    final AtomicReference<ScheduledFuture<?>> taskRef = new AtomicReference<>();
    ch.pipeline()
        .addLast(ActiveHandler.newHandler((handler, ctx, state) -> {
          log.info("{}, {}", ctx.channel().remoteAddress(), state);
          cancelTask(taskRef.getAndSet(null));
          if (ches.isEmpty()) {
            return;
          }
          if (state == ActiveHandler.State.ACTIVE) {
            // 连接，发送文件头
            CheFile che = ches.get(0);
            ch.writeAndFlush(Unpooled.wrappedBuffer(COPY.copy(che.cheHeader)))
                .addListener(f -> {
                  log.info("1. 发送文件头: {}, count: {}, sn: {}~{}"
                      , che.getName()
                      , che.length() / 576
                      , che.firstSn.getPackageSn()
                      , che.lastSn.getPackageSn()
                  );
                });
          } else {
            // 断开
          }
        }))
        .addLast(InboundHandler.newByteBufHandler((handler, ctx, msg) -> {
          byte[] data = handler.copy(msg);
          log.info("rcv: {}, {}, {}"
              , HexUtils.bytesToHex(data)
              , ctx.channel().remoteAddress()
              , ches.size()
          );
          if (ches.isEmpty()) {
            return;
          }
          CheFile che = ches.get(0);
          switch (data[4] & 0xFF) {
            case 0x01: {
              // 文件头反馈，是否开始上传
              if (data[5] == 0x01) {
                // 继续上传
                int resetSn = HexUtils.bytesToInt(data[6], data[7], data[8], data[9]);
                if (resetSn > 0 && !(resetSn >= che.firstSn.getPackageSn() && resetSn <= che.lastSn.getPackageSn())) {
                  // 删除
                  ctx.writeAndFlush(Unpooled.wrappedBuffer(wrapPacket(10, (byte) 0x03, (byte) 0x02, null)));
                  log.info("2. 删除错误的CHE: {}, curSn: {}, count: {}, sn: {} ~ {}"
                      , che.getName()
                      , che.curSn
                      , che.firstSn.getPackageSn() - che.lastSn.getPackageSn()
                      , che.firstSn.getPackageSn()
                      , che.lastSn.getPackageSn()
                  );
                  EventLoop.sleepSecond(1);
                  che.curSn = che.firstSn.getPackageSn() - 1;
                  // 发送包头
                  ctx.writeAndFlush(Unpooled.wrappedBuffer(che.cheHeader));
                  return;
                }
                che.curSn = Math.max(resetSn, che.firstSn.getPackageSn());
                log.info("3. {}, 重置上传位置, curSn: {}, sn: {} ~ {}"
                    , che.getName()
                    , che.curSn
                    , che.firstSn.getPackageSn()
                    , che.lastSn.getPackageSn()
                );
                if (options.useSchedule) {
                  if (taskRef.get() == null) {
                    taskRef.set(single.scheduleAtFixedRate(() -> {
                      if (ch.isActive()) {
                        sendBody(ch);
                      } else {
                        cancelTask(taskRef.getAndSet(null));
                      }
                    }, options.delay, options.delay, options.unit));
                  }
                } else {
                  if (taskRef.get() == null) {
                    taskRef.set(EventLoop.asyncIO(() -> {
                      EventLoop.sleepMillis(10);
                      while (taskRef.get() != null && ch.isActive()) {
                        CheFile cf = sendBody(ch);
                        if (cf != null) {
                          log.info("4. {}, 发送数据包, curSn: {}, sn: {} ~ {}"
                              , che.getName()
                              , che.curSn
                              , che.firstSn.getPackageSn()
                              , che.lastSn.getPackageSn()
                          );
                        } else {
                          break;
                        }
                      }
                      taskRef.set(null);
                    }));
                  }
                }
              } else {
                log.info("5. 未绑定患者: {}, firstSn: {}, lastSn: {}"
                    , che.getSource().getName()
                    , che.firstSn.getPackageSn()
                    , che.lastSn.getPackageSn()
                );
              }
            }
            break;
            case 0x02: {
              // 重置上传位置
              int resetSn = HexUtils.bytesToInt(data[6], data[7], data[8], data[9]);
              che.curSn = Math.max(resetSn - 1, che.firstSn.getPackageSn());
            }
            break;
            case 0x03: {
              // 继续下一个文件
              if (data[5] == 0x01) {
                if (che.curSn < che.lastSn.getPackageSn() - 1) {
                  log.info("6. 错误的文件，还未传完: {}, curSn: {}, count: {}, sn: {} ~ {}"
                      , che.getName()
                      , che.curSn
                      , che.lastSn.getPackageSn() - che.firstSn.getPackageSn()
                      , che.firstSn.getPackageSn()
                      , che.lastSn.getPackageSn()
                  );
                  return;
                }
                // 结束
                cancelTask(taskRef.getAndSet(null));
                EventLoop.sleepMillis(10); // 等待结束
                CheFile remove = ches.remove(0);
                File removeFile = remove.getSource();
                File dest = new File(removeFile.getParentFile(), removeFile.getName().replace(".CHE", ".HEX"));
                remove.close();
                FileCopy.cut(removeFile, dest);
                log.info("7. 重命名: {} \n\n", dest.getAbsolutePath());
                if (!dest.exists()) {
                  log.info("\n\n\n修改的文件不存在: {}\n\n\n", dest.getAbsolutePath());
                  stop(f -> {/*^_^*/});
                  System.exit(0);
                  return;
                }
                EventLoop.sleepSecond(1);
                // 继续下一个文件
                if (ches.isEmpty()) {
                  log.info("8. CHE上传结束...");
                  stop(f -> {/*^_^*/});
                  System.exit(0);
                } else {
                  // 发送下一个CHE
                  CheFile newChe = ches.get(0);
                  ch.writeAndFlush(Unpooled.wrappedBuffer(COPY.copy(newChe.cheHeader)))
                      .addListener(f -> {
                        log.info("9. 发送新文件头: {}, count: {}, sn: {}~{}"
                            , newChe.getName()
                            , newChe.length() / 576
                            , newChe.firstSn.getPackageSn()
                            , newChe.lastSn.getPackageSn()
                        );
                      });
                }
              } else if (data[5] == 0x02) {
                // 失败，重新传最后一个包
                che.curSn = che.lastSn.getPackageSn() - 2;
                ch.writeAndFlush(Unpooled.wrappedBuffer(COPY.copy(che.cheHeader)))
                    .addListener(f -> {
                      log.info("10. 发送新文件头: {}, count: {}, sn: {}~{}"
                          , che.getName()
                          , che.length() / 576
                          , che.firstSn.getPackageSn()
                          , che.lastSn.getPackageSn()
                      );
                    });
              }
            }
            break;
          }
        }))
    ;

  }

  private static void cancelTask(ScheduledFuture<?> sf) {
    EventLoop.cancel(sf);
  }


  private CheFile sendBody(Channel ch) {
    CheFile che = ches.get(0);
    // 发送
    // 逻辑计算对应包的位置：每个包576字节，假如包序号从1到100，偏移的包数据0，读取第5个包，则 包头(576) + (5 - 0 - 1)个包 * 576字节
    int sn = che.curSn;
    if (sn > che.lastSn.getPackageSn()) {
      // 发送结束包
      ch.writeAndFlush(Unpooled.wrappedBuffer(wrapPacket(8, (byte) 0x03)))
          .addListener(f -> {
            log.info("11. {}, 发送结束包, count: {}, sn: {}~{}, send_sn: {}"
                , che.getName()
                , che.length() / 576
                , che.firstSn.getPackageSn()
                , che.lastSn.getPackageSn()
                , sn
            );
          });
      return null;
    }
    try {
      long startAt = TimeUtils.now();
      // 读取
      int seekPos = (sn - che.firstSn.getPackageSn()) * 576;
      che.seek(seekPos);
      byte[] buf = wrapPacket(583, (byte) 0x02);
      if (che.read(buf, 7, 576) > 0) {
        CountDownLatch latch = new CountDownLatch(1);
        ch.writeAndFlush(Unpooled.wrappedBuffer(checkSum(buf)))
            .addListener(f -> {
              if (f.isSuccess()) {
                che.curSn = sn + 1;
              } else {
                log.info("12. {}, 发送失败, count: {}, sn: {}~{}, send_sn: {}, 耗时: {}"
                    , che.getName()
                    , che.length() / 576
                    , che.firstSn.getPackageSn()
                    , che.lastSn.getPackageSn()
                    , sn
                    , TimeUtils.diffNow(startAt)
                );
              }
              latch.countDown();
            });
        latch.await();
      } else {
        log.info("13. {}, 无法读取文件, sn: {}, pos: {}", che.getName(), sn, seekPos);
      }
    } catch (Exception e) {
      log.info("14. 发送失败: {}, count: {}, sn: {}~{}, error: {}"
          , che.getName()
          , che.length() / 576
          , che.firstSn.getPackageSn()
          , che.lastSn.getPackageSn()
          , e.getMessage()
      );
    }
    return che;
  }

  public static class CheFile extends RaFile {

    CheFileHeader header;
    HardwarePackage firstSn, lastSn;

    /**
     * 需要发送的文件头
     */
    final byte[] cheHeader;
    /**
     * 当前正在读取的SN
     */
    volatile int curSn = -1;

    public CheFile(File file) {
      super(file);
      this.header = CheHelper.parseHeader(getSource(), true);
      this.header.setRaw(COPY.copy(this.header.getRaw(), false));
      List<HardwarePackage> list = CheHelper.obtainFirstAndLast(getSource());
      this.firstSn = list.get(0);
      this.lastSn = list.get(1);

      byte[] raw = this.header.getRaw();
      // 包头
      // 文件长度
      byte[] snCountBytes = HexUtils.intToBytes((int) length());
      COPY.copy(snCountBytes, 0, raw, 0xA0, 4);
      // 第一个包序号
      byte[] firstSnBytes = HexUtils.intToBytes(firstSn.getPackageSn());
      COPY.copy(firstSnBytes, 0, raw, 0xA0 + 4, 4);
      // 文件名
      byte[] filenameBytes = getName().getBytes(StandardCharsets.UTF_8);
      COPY.copy(filenameBytes, 0, raw, 0xB0, filenameBytes.length);
      // 包数量
      COPY.copy(snCountBytes, 0, raw, raw.length - 4, 4);
      byte[] headerBytes = COPY.copy(raw, 0, wrapPacket(583, (byte) 0x01), 7, raw.length);
      this.cheHeader = checkSum(headerBytes);

      log.info("{}, file.len: {}, sn: {} ~ {}, headerBytes: {}"
          , file.getName()
          , file.length()
          , firstSn.getPackageSn()
          , lastSn.getPackageSn()
          , HexUtils.bytesToHex(headerBytes)
      );

//      if (firstSn.getPackageSn() <= 0 || lastSn.getPackageSn() <= 0) {
//        log.info("firstSn: {}", JSON.toJSONString(firstSn));
//        log.info("lastSn: {}", JSON.toJSONString(lastSn));
//
//        EventLoop.sleepSecond(1);
//        System.exit(0);
//        throw new IllegalStateException("CHE错误");
//      }
    }

    public String getName() {
      return getSource().getName();
    }


    public int read(byte[] buf, int offset, int len) throws IOException {
      return getRaf().read(buf, offset, len);
    }

  }


  static final ByteArrayCopy COPY = ByteArrayCopy.newBufCopy();

  /**
   * 包装数据
   *
   * @param len  长度
   * @param type 类型
   * @return 返回包装的数据
   */
  static byte[] wrapPacket(int len, byte type) {
    return wrapPacket(len, type, (byte) 0x00, null);
  }

  /**
   * 包装数据
   *
   * @param len  长度
   * @param type 类型
   * @return 返回包装的数据
   */
  static byte[] wrapPacket(int len, byte type, byte flag, byte[] payload) {
    byte[] data = new byte[len];
    data[0] = 0x55;
    data[1] = (byte) 0xAA;
    // 长度 2、3
    COPY.copy(HexUtils.shortToBytes((short) (len - 2)), 0, data, 2, 2);
    data[4] = type;
    if (flag > 0) {
      data[5] = flag;
    }
    if (payload != null && payload.length > 0) {
      COPY.copy(payload, 0, data, 7, payload.length);
    }
    // 校验和 5、6，7~582字节的校验和
    return checkSum(data);
  }

  /**
   * 计算校验和
   */
  static byte[] checkSum(byte[] data) {
    if (data.length > 8) {
      short sum = 0;
      for (int i = 7; i < data.length; i++) {
        sum += data[i];
      }
      byte[] sumBytes = HexUtils.shortToBytes(sum);
      data[5] = sumBytes[0];
      data[6] = sumBytes[1];
    }
    return data;
  }
}
