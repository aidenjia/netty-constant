package com.jiaz.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyServer {

  public static void main(String[] args) throws InterruptedException {
    /**
     * 1.boss 对应 IOServer.java 中的接受新连接线程，主要负责创建新连接
     * 2.worker 对应 IOServer.java 中的负责读取数据的线程，主要用于读取数据以及业务逻辑处理
     */
    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap
        .group(bossGroup, workerGroup)
        /**
         * 在ChannelInitializer这个类中有一个泛型参数NioSocketChannel,这个类就是
         * Netty对NIO类型的连接的抽象,而我们前面NioServerSocket也是对NIO类型的
         * 连接的抽象,NioServerSocketChannel和NioSocketChannel的概念可以喝BIO编程
         * 模型中ServerSocket以及Socket两个概念对应上
         */
        .channel(NioServerSocketChannel.class)
        .childAttr(AttributeKey.newInstance("childAttr"),"childAttrValue")
        .handler(new ServerHandler())
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
          @Override
          protected void initChannel(NioSocketChannel ch) {
            ch.pipeline().addLast(new FirstServerHandler());
          }
        });
    serverBootstrap.bind(20000).sync();
  }
}
