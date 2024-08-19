package com.benefitj.natproxy.udp;

import com.benefitj.core.HexUtils;
import com.benefitj.netty.client.UdpNettyClient;
import com.benefitj.netty.handler.ActiveHandler;
import com.benefitj.netty.handler.IdleStateEventHandler;
import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.netty.handler.ShutdownEventHandler;
import com.benefitj.netty.server.UdpNettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * UDP服务端
 */
@Component
public class UdpProxyServer extends UdpNettyServer {

  private UdpOptions options;
  /**
   * 远程主机地址
   */
  private final List<InetSocketAddress> remotes;
  /**
   * 客户端
   */
  private final AttributeKey<List<UdpClient>> clientsKey = AttributeKey.valueOf("clientsKey");
  private final AttributeKey<EventLoopGroup> groupKey = AttributeKey.valueOf("groupKey");

  @Autowired
  public UdpProxyServer(UdpOptions options) {
    this.options = options;
    this.remotes = Collections.synchronizedList(Arrays.stream(getOptions().getRemotes())
        .filter(StringUtils::isNotBlank)
        .map(s -> s.split(":"))
        .map(split -> new InetSocketAddress(split[0], Integer.parseInt(split[1])))
        .collect(Collectors.toList()));
  }

  @Override
  public UdpNettyServer useDefaultConfig() {
    UdpOptions ops = getOptions();
    this.useLinuxNativeEpoll(false);
    this.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(IdleStateEventHandler.newIdle(ops.getReaderTimeout(), ops.getWriterTimeout(), 0, TimeUnit.SECONDS))
            .addLast(IdleStateEventHandler.newCloseHandler())
            .addLast(ActiveHandler.newHandler((handler, ctx, state) -> {
              if (state.isActive()) {
                onClientChannelActive(ctx.channel());
              } else {
                onClientChannelInactive(ctx.channel());
              }
            }))
            .addLast(InboundHandler.newDatagramHandler((handler, ctx, msg) -> {
              List<UdpClient> clients = ctx.channel().attr(clientsKey).get();
              if (clients != null) {
                clients.forEach(c -> onSendRequest(c.getMainChannel(), handler, ctx, msg.copy()));
              } else {
                int size = Math.min(msg.content().readableBytes(), ops.getPrintRequestSize());
                log.warn("[udp] clients is empty, clientAddr: {}, remotes: {}, data: {}"
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
      NioEventLoopGroup group = new NioEventLoopGroup(1);
      List<UdpClient> clients = this.remotes.stream()
          .map(addr -> (UdpClient) new UdpClient()
              // 处理响应的数据
              .setInboundHandler(InboundHandler.newDatagramHandler(
                  (rhandler, rctx, rmsg) -> onSendResponse(realityChannel, rhandler, rctx, rmsg)))
              .group(group)
              .remoteAddress(addr)
              .start(f ->
                  log.info("[udp] client shadow started, reality: {}, shadow: {}, success: {}"
                      , realityChannel.remoteAddress(), addr, f.isSuccess())
              )
          )
          .collect(Collectors.toList());
      realityChannel.attr(clientsKey).set(clients);
      realityChannel.attr(groupKey).set(group);
    }
  }

  /**
   * 客户端下线
   *
   * @param realityChannel
   */
  protected void onClientChannelInactive(Channel realityChannel) {
    List<UdpClient> clients = realityChannel.attr(clientsKey).getAndSet(null);
    if (clients != null) {
      clients.forEach(c -> {
        try {
          c.closeMainChannel();
        } catch (Exception ignore) {
          /* ! */
        } finally {
          log.info("[udp] client shadow stopped, reality: {}, shadow: {}"
              , realityChannel.remoteAddress(), c.remoteAddress());
        }
      });
      realityChannel.attr(groupKey).getAndSet(null).shutdownGracefully();
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
    InetSocketAddress recipient = (InetSocketAddress) shadowChannel.remoteAddress();
    ByteBuf content = msg.content();
    DatagramPacket packet = new DatagramPacket(content, recipient);
    if (getOptions().isPrintRequest()) {
      int size = Math.min(getOptions().getPrintRequestSize(), content.readableBytes());
      byte[] data = handler.copyAndReset(content, size);
      shadowChannel.writeAndFlush(packet).addListener(f ->
          log.info("[udp] request reality: {}, shadow: {}, active: {}, data[{}]: {}, success: {}"
              , ctx.channel().remoteAddress()
              , shadowChannel.remoteAddress()
              , shadowChannel.isActive()
              , content.readableBytes()
              , HexUtils.bytesToHex(data)
              , f.isSuccess()
          ));
    } else {
      shadowChannel.writeAndFlush(packet);
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
                                InboundHandler<DatagramPacket> handler,
                                ChannelHandlerContext ctx,
                                DatagramPacket msg) {
    ByteBuf content = msg.content();
    DatagramPacket packet = new DatagramPacket(content.copy(), (InetSocketAddress) realityChannel.remoteAddress());
    if (getOptions().isPrintResponse()) {
      int size = Math.min(getOptions().getPrintResponseSize(), content.readableBytes());
      byte[] data = handler.copyAndReset(content, size);
      realityChannel.writeAndFlush(packet).addListener(f ->
          log.info("[udp] response reality: {}, shadow: {}, active: {}, data[{}]: {}, success: {}"
              , realityChannel.remoteAddress()
              , ctx.channel().localAddress()
              , realityChannel.isActive()
              , content.readableBytes()
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

  public UdpOptions getOptions() {
    return options;
  }

  public void setOptions(UdpOptions options) {
    this.options = options;
  }

  /**
   * UDP客户端
   */
  public static class UdpClient extends UdpNettyClient {

    private InboundHandler<DatagramPacket> inboundHandler;

    public UdpClient() {
    }

    public UdpClient setInboundHandler(InboundHandler<DatagramPacket> inboundHandler) {
      this.inboundHandler = inboundHandler;
      return this;
    }

    public InboundHandler<DatagramPacket> getInboundHandler() {
      return inboundHandler;
    }

    @Override
    public UdpNettyClient useDefaultConfig() {
      this.useLinuxNativeEpoll(false);
      this.handler(new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
          ch.pipeline()
              .addLast(ActiveHandler.newHandler((handler, ctx, state) ->
                  log.info("[udp] client active state change: {}, remote: {}", state, ch.remoteAddress())))
              .addLast(getInboundHandler())
              .addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                  if (cause instanceof PortUnreachableException) {
                    log.error("PortUnreachableException: " + ctx.channel().remoteAddress() + ", active: " + ctx.channel().isActive());
                  } else {
                    ctx.fireExceptionCaught(cause);
                  }
                }
              })
          ;
        }
      });
      return super.useDefaultConfig();
    }

    @Override
    protected ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners) {
      return bootstrap.connect().syncUninterruptibly().addListeners(listeners);
    }
  }

}
