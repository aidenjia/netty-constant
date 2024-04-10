package com.jiaz.transport.handler;

import static com.jiaz.constants.Constants.MAGIC;
import static com.jiaz.constants.Constants.VERSION_1;

import com.jiaz.heart_beat.HeartBeat;
import com.jiaz.protocol.Header;
import com.jiaz.protocol.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<Message<HeartBeat>> {

  public static final HeartBeatRequestHandler INSTANCE = new HeartBeatRequestHandler();

  private HeartBeatRequestHandler() {

  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Message<HeartBeat> msg){
    System.out.println("接收到心跳请求");
    Header header = new Header(MAGIC, VERSION_1, (byte) 0, 0L, 0);
    Message<HeartBeat> heartBeatMessage = new Message<>(header, new HeartBeat());
    ctx.writeAndFlush(heartBeatMessage);
  }
}
