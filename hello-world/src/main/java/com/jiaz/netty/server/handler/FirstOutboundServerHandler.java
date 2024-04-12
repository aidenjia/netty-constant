package com.jiaz.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;
import java.nio.charset.Charset;

public class FirstOutboundServerHandler extends ChannelOutboundHandlerAdapter {



  @Override
  public void bind(ChannelHandlerContext ctx, SocketAddress localAddress,
      ChannelPromise promise) throws Exception {
    System.out.println("FirstOutboundServerHandler.bind() called");
    super.bind(ctx, localAddress, promise);
  }

  @Override
  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
      SocketAddress localAddress, ChannelPromise promise) throws Exception {
    System.out.println("FirstOutboundServerHandler.connect() called");
    ctx.bind(localAddress, promise);
  }

  @Override
  public void flush(ChannelHandlerContext ctx) throws Exception {
    System.out.println("FirstOutboundServerHandler.flush() called");
    super.flush(ctx);
  }

  @Override
 public void deregister(ChannelHandlerContext ctx, ChannelPromise promise)
     throws Exception {
    System.out.println("FirstOutboundServerHandler.deregister() called");
    super.deregister(ctx, promise);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    ByteBuf byteBuf = (ByteBuf) msg;
    System.out.println(
        "FirstOutboundServerHandler.write() called" + byteBuf.toString(Charset.forName("UTF-8")));
    super.write(ctx, msg, promise);
  }
}
