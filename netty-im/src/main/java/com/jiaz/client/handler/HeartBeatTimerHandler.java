package com.jiaz.client.handler;

import com.jiaz.protocol.request.HeartBeatRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.TimeUnit;

public class HeartBeatTimerHandler extends ChannelInboundHandlerAdapter {

  private static final int HEARTBEAT_INTERVAL = 5;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    scheduleSendHeartBeat(ctx);

    super.channelActive(ctx);
  }

  private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
    ctx.executor().schedule(() -> {

      if (ctx.channel().isActive()) {
        System.out.println("发送心跳请求");
        ctx.writeAndFlush(new HeartBeatRequestPacket());
        scheduleSendHeartBeat(ctx);
      }

    }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
  }
}
