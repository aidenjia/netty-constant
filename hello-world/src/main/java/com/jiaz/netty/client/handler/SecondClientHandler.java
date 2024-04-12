package com.jiaz.netty.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.charset.Charset;
import java.util.Date;

public class SecondClientHandler extends SimpleChannelInboundHandler {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf byteBuf = (ByteBuf) msg;
    System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.forName("UTF-8")));
  }
}
