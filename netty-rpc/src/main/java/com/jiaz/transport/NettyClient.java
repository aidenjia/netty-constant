package com.jiaz.transport;

import com.jiaz.codec.RpcDecoder;
import com.jiaz.codec.RpcEncoder;
import com.jiaz.constants.Constants;
import com.jiaz.transport.handler.RpcClientHandler;
import com.jiaz.transport.handler.RpcIdleStateHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.Closeable;
import java.io.IOException;

public class NettyClient implements Closeable {

  protected Bootstrap clientBootstrap;
  protected EventLoopGroup group;
  private String host;
  private int port;

  public NettyClient(String host, int port) {
    this.host = host;
    this.port = port;
    // 创建并配置客户端Bootstrap
    clientBootstrap = new Bootstrap();
    group = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS, "NettyClientWorker");
    clientBootstrap.group(group)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .channel(NioSocketChannel.class) // 创建的Channel类型
        // 指定ChannelHandler的顺序
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new RpcIdleStateHandler());
            ch.pipeline().addLast("demo-rpc-encoder", new RpcEncoder());
            ch.pipeline().addLast("demo-rpc-decoder", new RpcDecoder());
            ch.pipeline().addLast("client-handler", new RpcClientHandler());
          }
        });
  }

  public ChannelFuture connect() {
    ChannelFuture connectFuture = clientBootstrap.connect(host, port);
    connectFuture.awaitUninterruptibly();
    return connectFuture;
  }


  @Override
  public void close() throws IOException {
    System.out.println("shutdownGracefully");
    group.shutdownGracefully();
  }
}
