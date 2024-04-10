package com.jiaz.transport.handler;

import com.jiaz.constants.Constants;
import com.jiaz.protocol.Message;
import com.jiaz.protocol.Response;
import com.jiaz.transport.Connection;
import com.jiaz.transport.NettyResponseFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<Message<Response>> {

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext,
      Message<Response> message) throws Exception {
    NettyResponseFuture responseFuture =
        Connection.IN_FLIGHT_REQUEST_MAP.remove(message.getHeader().getMessageId());
    Response response = message.getContent();
    if (response == null && Constants.isHeartBeat(message.getHeader().getExtraInfo())) {
      response = new Response();
      response.setCode(Constants.HEARTBEAT_CODE);
    }
    responseFuture.getPromise().setSuccess(response.getResult());
  }
}
