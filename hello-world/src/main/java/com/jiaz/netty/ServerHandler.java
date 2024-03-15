package com.jiaz.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    System.out.println("channel Active");
  }

  @Override
  public void channelRegistered(ChannelHandlerContext context) {
    System.out.println("channelRegistered");
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) {
    System.out.println("handlerAdded");
  }
}
