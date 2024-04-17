package com.rpc.plus.handler;

import com.rpc.plus.RpcFuture;
import com.rpc.plus.RpcRequestHolder;
import com.rpc.plus.RpcResponse;
import com.rpc.plus.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcResponseHandler extends
    SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg)
      throws Exception {
    long requestId = msg.getHeader().getRequestId();
    RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
    future.getPromise().setSuccess(msg.getBody());
  }
}
