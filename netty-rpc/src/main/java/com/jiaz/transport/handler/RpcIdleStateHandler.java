package com.jiaz.transport.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

public class RpcIdleStateHandler extends IdleStateHandler {

  private static final int ALL_IDLE_TIME = 60 * 2;

  public RpcIdleStateHandler() {
    super(0, 0, ALL_IDLE_TIME, TimeUnit.SECONDS);
  }

  @Override
  protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
    System.out.println(ALL_IDLE_TIME + "秒内未读写到数据，关闭连接");
    ctx.channel().close();
  }
}
