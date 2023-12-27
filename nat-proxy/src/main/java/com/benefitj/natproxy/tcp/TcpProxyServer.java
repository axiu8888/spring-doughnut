package com.benefitj.natproxy.tcp;

import com.benefitj.core.HexUtils;
import com.benefitj.netty.client.TcpNettyClient;
import com.benefitj.netty.handler.ActiveHandler;
import com.benefitj.netty.handler.IdleStateEventHandler;
import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.netty.handler.ShutdownEventHandler;
import com.benefitj.netty.server.TcpNettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * TCP 服务端
 */
public class TcpProxyServer extends TcpNettyServer {

  private TcpOptions options;
  /**
   * 远程主机地址
   */
  private final List<InetSocketAddress> remotes;
  /**
   * 客户端
   */
  private final AttributeKey<List<TcpClient>> clientsKey = AttributeKey.valueOf("clientsKey");

  public TcpProxyServer(TcpOptions options) {
    this.options = options;
    this.remotes = Collections.synchronizedList(Arrays.stream(getOptions().getRemotes())
        .filter(StringUtils::isNotBlank)
        .map(s -> s.split(":"))
        .map(split -> new InetSocketAddress(split[0], Integer.parseInt(split[1])))
        .collect(Collectors.toList()));
  }

  @Override
  public TcpNettyServer useDefaultConfig() {
    this.useLinuxNativeEpoll(false);
    this.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        final TcpOptions ops = TcpProxyServer.this.options;
        ch.pipeline()
            .addLast(ShutdownEventHandler.INSTANCE)
            .addLast(IdleStateEventHandler.newIdle(ops.getReaderTimeout(), ops.getWriterTimeout(), 0, TimeUnit.SECONDS))
            .addLast(IdleStateEventHandler.newCloseHandler())
            .addLast(ActiveHandler.newHandler((handler, ctx, state) -> {
              if (state.isActive()) {
                onClientChannelActive(ctx.channel());
              } else {
                onClientChannelInactive(ctx.channel());
              }
            }))
            .addLast(InboundHandler.newByteBufHandler((handler, ctx, msg) -> {
              List<TcpClient> clients = ctx.channel().attr(clientsKey).get();
              if (clients != null && !clients.isEmpty()) {
                clients.forEach(c -> c.useServeChannel(ch2 -> onSendRequest(ch2, handler, ctx, msg.copy())));
              } else {
                int size = Math.min(msg.readableBytes(), ops.getPrintRequestSize());
                log.warn("tcp clients is empty, clientAddr: {}, remotes: {}, data: {}"
                    , ctx.channel().remoteAddress()
                    , ops.getRemotes()
                    , HexUtils.bytesToHex(handler.copyAndReset(msg, size))
                );
              }
            }))
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
      // 创建TCP客户端
      NioEventLoopGroup group = new NioEventLoopGroup(1);
      TcpOptions ops = this.getOptions();
      List<TcpClient> clients = this.remotes.stream()
          .map(addr -> (TcpClient) new TcpClient()
              .setActiveHandler(ops.isFastFailover() ?
                  ActiveHandler.newHandler((handler, ctx, state) -> {
                    if (!state.isActive() && realityChannel.isActive()) {
                      realityChannel.close();
                    }
                  }) : null)
              // 处理响应的数据
              .setInboundHandler(InboundHandler.newByteBufHandler(
                  (rhandler, rctx, rmsg) -> onSendResponse(realityChannel, rhandler, rctx, rmsg)))
              .group(group)
              .remoteAddress(addr)
              .autoReconnect(ops.isAutoReconnect(), ops.getReconnectDelay(), TimeUnit.SECONDS)
              .start(f ->
                  log.info("[tcp] client shadow started, reality: {}, shadow: {}, success: {}"
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
    List<TcpClient> clients = realityChannel.attr(clientsKey).getAndSet(null);
    if (clients != null) {
      clients.forEach(c ->
          c.stop(f ->
              log.info("[tcp] client shadow stopped, reality: {}, shadow: {}"
                  , realityChannel.remoteAddress(), c.remoteAddress())
          )
      );
    }
  }

  /**
   * 发送请求
   *
   * @param shadowChannel 代理连接的通道
   * @param handler       处理
   * @param ctx           上下文
   * @param msg           消息
   */
  protected void onSendRequest(Channel shadowChannel,
                               InboundHandler<ByteBuf> handler,
                               ChannelHandlerContext ctx,
                               ByteBuf msg) {
    if (!shadowChannel.isActive()) {
      return;
    }
    if (msg.refCnt() <= 0) {
      ReferenceCountUtil.retain(msg);
    }
    if (getOptions().isPrintRequest()) {
      int size = Math.min(getOptions().getPrintRequestSize(), msg.readableBytes());
      byte[] data = handler.copyAndReset(msg, size);
      shadowChannel.writeAndFlush(msg).addListener(f ->
          log.info("[tcp] request reality: {}, shadow: {}, active: {}, data[{}]: {}, success: {}"
              , ctx.channel().remoteAddress()
              , shadowChannel.remoteAddress()
              , shadowChannel.isActive()
              , msg.readableBytes()
              , HexUtils.bytesToHex(data)
              , f.isSuccess()
          ));
    } else {
      shadowChannel.writeAndFlush(msg);
    }
  }

  /**
   * 发送到响应
   *
   * @param realityChannel 远程客户端通道
   * @param handler        处理
   * @param ctx            上下文
   * @param msg            消息
   */
  protected void onSendResponse(Channel realityChannel,
                                InboundHandler<ByteBuf> handler,
                                ChannelHandlerContext ctx,
                                ByteBuf msg) {
    ByteBuf copy = msg.copy();
    if (getOptions().isPrintResponse()) {
      int size = Math.min(getOptions().getPrintResponseSize(), copy.readableBytes());
      byte[] data = handler.copyAndReset(copy, size);
      realityChannel.writeAndFlush(copy).addListener(f ->
          log.info("[tcp] response reality: {}, shadow: {}, active: {}, data[{}]: {}, success: {}"
              , realityChannel.remoteAddress()
              , ctx.channel().localAddress()
              , realityChannel.isActive()
              , msg.readableBytes()
              , HexUtils.bytesToHex(data)
              , f.isSuccess()
          ));
    } else {
      realityChannel.writeAndFlush(copy);
    }
  }

  @Override
  public TcpNettyServer stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    return super.stop(listeners);
  }

  public TcpOptions getOptions() {
    return options;
  }

  public void setOptions(TcpOptions options) {
    this.options = options;
  }

  /**
   * TCP客户端
   */
  public static class TcpClient extends TcpNettyClient {

    private InboundHandler<ByteBuf> inboundHandler;
    private ActiveHandler activeChannelHandler;

    public TcpClient() {
    }

    public TcpClient setInboundHandler(InboundHandler<ByteBuf> inboundHandler) {
      this.inboundHandler = inboundHandler;
      return this;
    }

    public InboundHandler<ByteBuf> getInboundHandler() {
      return inboundHandler;
    }

    public ActiveHandler getActiveHandler() {
      return activeChannelHandler;
    }

    public TcpClient setActiveHandler(ActiveHandler activeChannelHandler) {
      this.activeChannelHandler = activeChannelHandler;
      return this;
    }

    @Override
    public TcpNettyClient useDefaultConfig() {
      this.useLinuxNativeEpoll(false);
      // 监听
      this.handler(new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
          ActiveHandler acch = getActiveHandler();
          if (acch != null) {
            ch.pipeline().addLast(acch);
          }
          ch.pipeline()
              .addLast(ActiveHandler.newHandler((handler, ctx, state) ->
                  log.info("[tcp] client active change, state: {}, remote: {}", state, ch.remoteAddress())))
              .addLast(getInboundHandler())
              .addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                  if (cause instanceof PortUnreachableException) {
                    Channel ch = ctx.channel();
                    log.error("PortUnreachableException: " + ch.remoteAddress() + ", active: " + ch.isActive());
                  } else {
                    log.warn("[tcp] exceptionCaught: {}", cause.getMessage());
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
