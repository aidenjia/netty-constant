package com.rpc.plus.handler;

import com.rpc.plus.RpcRequest;
import com.rpc.plus.RpcResponse;
import com.rpc.plus.RpcServiceHelper;
import com.rpc.plus.protocol.MsgHeader;
import com.rpc.plus.protocol.MsgStatus;
import com.rpc.plus.protocol.MsgType;
import com.rpc.plus.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

  private final Map<String, Object> rpcServiceMap;

  public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
    this.rpcServiceMap = rpcServiceMap;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
    RpcRequestProcessor.submitRequest(()->{
      RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
      RpcResponse response = new RpcResponse();
      MsgHeader header = protocol.getHeader();
      header.setMsgType((byte) MsgType.RESPONSE.getType());
      try {
        Object result = handle(protocol.getBody());
        response.setData(result);

        header.setStatus((byte) MsgStatus.SUCCESS.getCode());
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(response);
      } catch (Throwable throwable) {
        header.setStatus((byte) MsgStatus.FAIL.getCode());
        response.setMessage(throwable.toString());
        log.error("process request {} error", header.getRequestId(), throwable);
      }
      ctx.writeAndFlush(responseRpcProtocol);
    });
  }

  private Object handle(RpcRequest request) throws Throwable {
    String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
    Object serviceBean = rpcServiceMap.get(serviceKey);
    if (serviceBean == null) {
      throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
    }
    Class<?> serviceClass = serviceBean.getClass();
    String methodName = request.getMethodName();
    Class<?>[] parameterTypes = request.getParameterTypes();
    Object[] parameters = request.getParams();
    FastClass fastClass = FastClass.create(serviceClass);
    int methodIndex = fastClass.getIndex(methodName, parameterTypes);
    return fastClass.invoke(methodIndex, serviceBean, parameters);
  }
}
