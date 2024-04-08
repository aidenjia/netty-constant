package com.jiaz.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LifeCyCleTestHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    System.out.println("逻辑处理器被添加：handlerAdded()");
    super.handlerAdded(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channel 绑定到线程(NioEventLoop)：channelRegistered()");
    super.channelRegistered(ctx);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channel 准备就绪：channelActive()");
    super.channelActive(ctx);
  }

  /**
   * channelRead()和channelReadComplete()不总是成对出现，
   * 可能好几个channelReade()才会有一个channelReadComplete()出现。
   * 比如某次连续10个分包读取，每读取一个分包就会channelRead()，
   * 但读取完全部10个分包才会一次channelReadComplete()
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("channel 有数据可读：channelRead()");
    super.channelRead(ctx, msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channel 某次数据读完：channelReadComplete()");
    super.channelReadComplete(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channel 被关闭：channelInactive()");
    super.channelInactive(ctx);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channel 取消线程(NioEventLoop) 的绑定: channelUnregistered()");
    super.channelUnregistered(ctx);
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    System.out.println("逻辑处理器被移除：handlerRemoved()");
    super.handlerRemoved(ctx);
  }
}
