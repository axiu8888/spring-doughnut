package com.benefitj.natproxy.udptcp;

import com.benefitj.core.HexUtils;
import com.benefitj.netty.client.TcpNettyClient;
import com.benefitj.netty.handler.ActiveHandler;
import com.benefitj.netty.handler.IdleStateEventHandler;
import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.netty.handler.ShutdownEventHandler;
import com.benefitj.netty.server.UdpNettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * UDP服务端
 */
@Component
public class UdpTcpProxyServer extends UdpNettyServer {

  private UdpTcpOptions options;
  /**
   * 远程主机地址
   */
  private final List<InetSocketAddress> remotes;
  /**
   * 客户端
   */
  private final AttributeKey<List<UdpTcpClient>> clientsKey = AttributeKey.valueOf("clientsKey");

  public UdpTcpProxyServer(UdpTcpOptions options) {
    this.options = options;
    this.remotes = Collections.synchronizedList(Arrays.stream(getOptions().getRemotes())
        .filter(StringUtils::isNotBlank)
        .map(s -> s.split(":"))
        .map(split -> new InetSocketAddress(split[0], Integer.parseInt(split[1])))
        .collect(Collectors.toList()));
  }

  @Override
  public UdpNettyServer useDefaultConfig() {
    UdpTcpOptions ops = getOptions();
    this.useLinuxNativeEpoll(false);
    this.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(IdleStateEventHandler.newIdle(ops.getReaderTimeout(), ops.getWriterTimeout(), 0, TimeUnit.SECONDS))
            .addLast(IdleStateEventHandler.newCloseHandler())
            .addLast(ActiveHandler.newHandler((handler, ctx, state) -> {
              //log.info("udp active state change: {}, remote: {}", state, ctx.channel().remoteAddress());
              if (state == ActiveHandler.State.ACTIVE) {
                onClientChannelActive(ctx.channel());
              } else {
                onClientChannelInactive(ctx.channel());
              }
            }))
            .addLast(InboundHandler.newDatagramHandler((handler, ctx, msg) -> {
              List<UdpTcpClient> clients = ctx.channel().attr(clientsKey).get();
              if (clients != null) {
                clients.forEach(client -> onSendRequest(client.getMainChannel(), handler, ctx, msg));
              } else {
                int size = Math.min(msg.content().readableBytes(), ops.getPrintRequestSize());
                log.warn("[udp-tcp] clients is empty, clientAddr: {}, remotes: {}, data: {}"
                    , ctx.channel().remoteAddress()
                    , ops.getRemotes()
                    , HexUtils.bytesToHex(handler.copyAndReset(msg, size))
                );
              }
            }))
            .addLast(ShutdownEventHandler.INSTANCE)
            .addLast(new ChannelInboundHandlerAdapter() {
              @Override
              public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                cause.printStackTrace();
              }
            })
        ;
      }
    });
    return super.useDefaultConfig();
  }

  /**
   * 客户端上线
   *
   * @param realityChannel
   */
  protected void onClientChannelActive(Channel realityChannel) {
    if (!realityChannel.hasAttr(clientsKey)) {
      // 创建UDP客户端
      final UdpTcpOptions ops = getOptions();
      NioEventLoopGroup group = new NioEventLoopGroup(1);
      List<UdpTcpClient> clients = this.remotes.stream()
          .map(addr -> (UdpTcpClient) new UdpTcpClient()
              // 处理响应的数据
              .setInboundHandler(InboundHandler.newByteBufHandler(
                  (rhandler, rctx, rmsg) -> onSendResponse(realityChannel, rhandler, rctx, rmsg)))
              .group(group)
              .remoteAddress(addr)
              .autoReconnect(ops.getAutoReconnect(), Duration.ofSeconds(ops.getReconnectDelay()))
              .start(f ->
                  log.info("[udp-tcp] client shadow started, reality: {}, shadow: {}, success: {}"
                      , realityChannel.remoteAddress(), addr, f.isSuccess())
              )
          )
          .collect(Collectors.toList());
      realityChannel.attr(clientsKey).set(clients);
    }
  }

  /**
   * 客户端下线
   *
   * @param realityChannel
   */
  protected void onClientChannelInactive(Channel realityChannel) {
    List<UdpTcpClient> clients = realityChannel.attr(clientsKey).getAndSet(null);
    if (clients != null) {
      clients.forEach(c ->
          c.stop(f ->
              log.info("[udp-tcp] client shadow stopped, reality: {}, shadow: {}"
                  , realityChannel.remoteAddress(), c.remoteAddress()))
      );
    }
  }

  /**
   * 发送请求
   *
   * @param shadowChannel 转发的连接的通道
   * @param handler       处理
   * @param ctx           上下文
   * @param msg           消息
   */
  protected void onSendRequest(Channel shadowChannel,
                               InboundHandler<DatagramPacket> handler,
                               ChannelHandlerContext ctx,
                               DatagramPacket msg) {
    if (shadowChannel.isOpen()) {
      EventLoop eventLoop = shadowChannel.eventLoop();
      if (eventLoop.inEventLoop()) {
        ByteBuf content = msg.content();
        if (getOptions().isPrintRequest()) {
          int readableBytes = content.readableBytes();
          int size = Math.min(getOptions().getPrintRequestSize(), readableBytes);
          byte[] data = handler.copyAndReset(content, size);
          shadowChannel.writeAndFlush(content).addListener(f ->
              log.info("[udp-tcp] request reality: {}, shadow: {}, active: {}, data[{}]: {}, success: {}"
                  , ctx.channel().remoteAddress()
                  , shadowChannel.remoteAddress()
                  , shadowChannel.isActive()
                  , readableBytes
                  , HexUtils.bytesToHex(data)
                  , f.isSuccess()
              ));
        } else {
          shadowChannel.writeAndFlush(content);
        }
      } else {
        final DatagramPacket retain = msg.retain();
        if (shadowChannel.isActive()) {
          eventLoop.execute(() -> onSendRequest(shadowChannel, handler, ctx, retain));
        } else {
          eventLoop.schedule(() -> onSendRequest(shadowChannel, handler, ctx, retain), 10, TimeUnit.MILLISECONDS);
        }
      }
    }
  }

  /**
   * 发送到响应
   *
   * @param realityChannel 通道
   * @param handler        处理
   * @param ctx            上下文
   * @param msg            消息
   */
  protected void onSendResponse(Channel realityChannel,
                                InboundHandler<ByteBuf> handler,
                                ChannelHandlerContext ctx,
                                ByteBuf msg) {
    DatagramPacket packet = new DatagramPacket(msg.copy(), (InetSocketAddress) realityChannel.remoteAddress());
    if (getOptions().isPrintResponse()) {
      int readableBytes = msg.readableBytes();
      int size = Math.min(getOptions().getPrintResponseSize(), readableBytes);
      byte[] data = handler.copyAndReset(msg, size);
      realityChannel.writeAndFlush(packet).addListener(f ->
          log.info("[udp-tcp] response reality: {}, shadow: {}, active: {}, data[{}]: {}, success: {}"
              , realityChannel.remoteAddress()
              , ctx.channel().localAddress()
              , realityChannel.isActive()
              , readableBytes
              , HexUtils.bytesToHex(data)
              , f.isSuccess()
          ));
    } else {
      realityChannel.writeAndFlush(packet);
    }
  }

  @Override
  public UdpNettyServer stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    return super.stop(listeners);
  }

  public UdpTcpOptions getOptions() {
    return options;
  }

  public void setOptions(UdpTcpOptions options) {
    this.options = options;
  }

  /**
   * TCP客户端
   */
  public static class UdpTcpClient extends TcpNettyClient {

    private InboundHandler<ByteBuf> inboundHandler;

    public UdpTcpClient() {
    }

    public UdpTcpClient setInboundHandler(InboundHandler<ByteBuf> inboundHandler) {
      this.inboundHandler = inboundHandler;
      return this;
    }

    public InboundHandler<ByteBuf> getInboundHandler() {
      return inboundHandler;
    }

    @Override
    public TcpNettyClient useDefaultConfig() {
      this.useLinuxNativeEpoll(false);
      // 监听
      this.handler(new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
          ch.pipeline()
              .addLast(ActiveHandler.newHandler((handler, ctx, state) -> log.info("[udp-[tcp]] client active change, state: {}, remote: {}", state, ch.remoteAddress())))
              .addLast(getInboundHandler())
              .addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                  if (cause instanceof PortUnreachableException) {
                    Channel ch = ctx.channel();
                    log.error("PortUnreachableException: " + ch.remoteAddress() + ", active: " + ch.isActive());
                  } else {
                    log.warn("[udp-[tcp]]exceptionCaught: {}", cause.getMessage());
                    //ctx.fireExceptionCaught(cause);
                  }
                }
              })
          ;
        }
      });
      return super.useDefaultConfig();
    }

  }

}
