package com.jiaz.transport.handler;

import com.jiaz.constants.Constants;
import com.jiaz.protocol.Message;
import com.jiaz.protocol.Request;
import com.jiaz.transport.InvokeRunnable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RpcServerHandler extends SimpleChannelInboundHandler<Message<Request>> {

  // 业务线程池
  private static Executor executor = Executors.newCachedThreadPool();

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message<Request> message) throws Exception {
    byte extraInfo = message.getHeader().getExtraInfo();
    if (Constants.isHeartBeat(extraInfo)) { // 心跳消息，直接返回即可
      channelHandlerContext.writeAndFlush(message);
      return;
    }
    executor.execute(new InvokeRunnable(message, channelHandlerContext));
  }
}
