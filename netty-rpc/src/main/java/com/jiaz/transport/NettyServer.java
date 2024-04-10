package com.jiaz.transport;

import com.jiaz.codec.RpcDecoder;
import com.jiaz.codec.RpcEncoder;
import com.jiaz.transport.handler.HeartBeatRequestHandler;
import com.jiaz.transport.handler.RpcIdleStateHandler;
import com.jiaz.transport.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ServerBootstrap serverBootstrap;
  protected int port;

  private Channel channel;


  public NettyServer(int port) {
    this.port = port;
    bossGroup = NettyEventLoopFactory.eventLoopGroup(1,
        "NettyServerBoss");
    workerGroup = NettyEventLoopFactory.eventLoopGroup(
        Math.min(Runtime.getRuntime().availableProcessors() + 1, 32),
        "NettyServerWorker");
    serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        //地址重用: 当 SO_REUSEADDR 被设置为 true 时，表示允许绑定到同一端口的多个 Socket 连接。通常情况下，如果一个 Socket 连接处于 TIME_WAIT 状态（等待关闭的连接），
        // 那么在同一端口上启动新的 Socket 连接会失败，因为操作系统会认为端口仍然被占用。启用 SO_REUSEADDR 选项可以绕过这个限制，允许多个 Socket 连接绑定到同一端口.
        .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
        //当服务器正在处理连接请求时，如果新的连接请求到达，但服务器无法立即处理，
        // 这些连接请求将会被放置到一个队列中等待处理。SO_BACKLOG 选项允许你设置这个等待队列的最大长度
        .option(ChannelOption.SO_BACKLOG, 1024)
        //Nagle 算法是一种用于优化 TCP 连接的算法，通过合并小的数据包以减少网络上的数据包数量，从而提高网络利用率
        .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
        //在 Netty 中，ChannelOption.ALLOCATOR 是一个 Channel 配置选项，用于指定用于分配 ByteBuf 的分配器（Allocator）。具体作用如下：
        //控制 ByteBuf 的内存分配器：Netty 提供了多种不同类型的内存分配器，比如 PooledByteBufAllocator 和 UnpooledByteBufAllocator。ChannelOption.ALLOCATOR 允许你指定在创建新的 Channel 时使用的内存分配器。
        //优化内存使用：选择合适的内存分配器可以根据应用的需求优化内存的使用。PooledByteBufAllocator 使用池化的方式管理 ByteBuf，可以减少内存碎片和减少内存分配的开销，适用于高性能的应用场景。
        //定制内存管理策略：通过设置 ChannelOption.ALLOCATOR，你可以定制自己的内存管理策略，比如通过实现自定义的 ByteBufAllocator 接口来实现特定的内存分配策略
        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        //当 SO_KEEPALIVE 被设置为 true 时，表示启用 TCP 连接的保活机制。TCP 保活机制是一种机制，
        // 用于检测连接是否仍然有效。在连接空闲一段时间后，系统会发送一系列的探测报文给对端，如果对端没有响应，就会认为连接已经断开
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new RpcIdleStateHandler());
            ch.pipeline().addLast("rpc-decoder", new RpcDecoder());
           // ch.pipeline().addLast(HeartBeatRequestHandler.INSTANCE);
            ch.pipeline().addLast("rpc-encoder", new RpcEncoder());
            ch.pipeline().addLast("server-handler", new RpcServerHandler());
          }
        });

    try {
      Channel ch = serverBootstrap.bind(port).sync().channel();
      ch.closeFuture().sync();
    } catch (InterruptedException e) {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }


  public void shutdown() throws InterruptedException {
    channel.close().sync();
    if (bossGroup != null) {
      bossGroup.shutdownGracefully().awaitUninterruptibly(15000);
    }
    if (workerGroup != null) {
      workerGroup.shutdownGracefully().awaitUninterruptibly(15000);
    }
  }

}
