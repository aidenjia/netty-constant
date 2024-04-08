package com.jiaz.server;

import com.jiaz.codec.PacketDecoder;
import com.jiaz.codec.PacketEncoder;
import com.jiaz.server.handler.AuthHandler;
import com.jiaz.server.handler.LoginRequestHandler;
import com.jiaz.server.handler.MessageRequestHandler;
import com.jiaz.server.handler.Spliter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Date;

public class NettyServer {

  private static final int PORT = 8000;

  public static void main(String[] args) {
    NioEventLoopGroup boosGroup = new NioEventLoopGroup();
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    final ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(boosGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        // 在Netty中，ChannelOption.SO_BACKLOG 是一个配置项，用于设置服务器端套接字接受连接的最大队列长度。它的作用如下：
        //
        //控制连接的排队: 当服务器端处理连接请求的速度不足以及时处理新的连接请求时，新的连接请求将会进入等待队列。SO_BACKLOG 就是用来指定这个等待队列的最大长度的。
        //
        //优化连接处理: 通过设置适当的 SO_BACKLOG 值，可以避免服务器端因为连接请求过多而导致连接处理的性能下降。如果队列已满，新的连接请求将会被拒绝，而不会继续等待。
        //
        //提高系统的可靠性: 通过控制连接请求的队列长度，可以在一定程度上保护服务器端免受连接请求的过载。
        .option(ChannelOption.SO_BACKLOG, 1024)
        //在 Netty 中，ChannelOption.SO_KEEPALIVE 是一个 Socket 选项，用于设置 TCP 连接是否保持活跃状态。它的作用如下：
        //
        //保持连接活跃: 当 SO_KEEPALIVE 被设置为 true 时，TCP 连接会周期性地发送探测报文给对端，以确保连接的存活性。这对于长时间没有数据交互的连接来说是非常有用的，可以避免由于网络故障或者其他原因导致连接断开。
        //
        //检测连接状态: 通过启用 SO_KEEPALIVE，服务器端和客户端可以及时地检测到连接断开或者对方异常退出的情况，从而及时地进行处理。
        //
        //减少资源浪费: 如果一个连接已经不再活跃，但仍然保持着连接状态，这会浪费网络和系统资源。通过启用 SO_KEEPALIVE，可以及时地释放这些资源。
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        //在 Netty 中，ChannelOption.TCP_NODELAY 是一个 Socket 选项，用于控制是否启用 Nagle 算法。Nagle 算法是一种在发送 TCP 数据包时对数据进行延迟发送以优化网络利用率的算法。该选项的作用如下：
        //
        //控制是否启用 Nagle 算法：当 TCP_NODELAY 被设置为 true 时，表示禁用 Nagle 算法，即数据立即发送。这对于对延迟敏感的应用来说是很重要的，比如实时通信、在线游戏等。
        //
        //减少延迟：通过禁用 Nagle 算法，可以减少发送数据的延迟，提高数据传输的实时性。
        //
        //适用于小数据包场景：Nagle 算法通常会将小数据包缓冲一段时间，然后合并成一个较大的数据包再发送，这样会导致一定的延迟。在对小数据包的传输有较高要求的场景中，禁用 Nagle 算法可以更快地传输数据。
        .childOption(ChannelOption.TCP_NODELAY, true)
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
          protected void initChannel(NioSocketChannel ch) {
            ch.pipeline().addLast(new Spliter());
            ch.pipeline().addLast(new PacketDecoder());
            ch.pipeline().addLast(new LoginRequestHandler());
            ch.pipeline().addLast(new AuthHandler());
            ch.pipeline().addLast(new MessageRequestHandler());
            ch.pipeline().addLast(new PacketEncoder());
          }
        });
    bind(serverBootstrap, PORT);
  }

  private static void bind(final ServerBootstrap serverBootstrap, final int port) {
    serverBootstrap.bind(port).addListener(future -> {
      if (future.isSuccess()) {
        System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
      } else {
        System.err.println("端口[" + port + "]绑定失败!");
      }
    });
  }
}
