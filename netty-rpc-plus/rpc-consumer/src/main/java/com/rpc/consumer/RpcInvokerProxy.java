package com.rpc.consumer;

import com.rpc.plus.RegistryService;
import com.rpc.plus.RpcFuture;
import com.rpc.plus.RpcRequest;
import com.rpc.plus.RpcRequestHolder;
import com.rpc.plus.RpcResponse;
import com.rpc.plus.protocol.MsgHeader;
import com.rpc.plus.protocol.MsgType;
import com.rpc.plus.protocol.ProtocolConstants;
import com.rpc.plus.protocol.RpcProtocol;
import com.rpc.plus.serialization.SerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class RpcInvokerProxy implements InvocationHandler {

  private final String serviceVersion;
  private final long timeout;
  private final RegistryService registryService;

  public RpcInvokerProxy(String serviceVersion, long timeout, RegistryService registryService) {
    this.serviceVersion = serviceVersion;
    this.timeout = timeout;
    this.registryService = registryService;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
    long requestId = RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
    MsgHeader header = new MsgHeader();
    header.setMagic(ProtocolConstants.MAGIC);
    header.setVersion(ProtocolConstants.VERSION);
    header.setRequestId(requestId);
    header.setSerialization((byte) SerializationTypeEnum.HESSIAN.getType());
    header.setMsgType((byte) MsgType.REQUEST.getType());
    header.setStatus((byte) 0x1);
    rpcProtocol.setHeader(header);
    RpcRequest request = new RpcRequest();
    request.setServiceVersion(this.serviceVersion);
    request.setClassName(method.getDeclaringClass().getName());
    request.setMethodName(method.getName());
    request.setParameterTypes(method.getParameterTypes());
    request.setParams(args);
    rpcProtocol.setBody(request);
    RpcConsumer rpcConsumer = new RpcConsumer();
    RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()),
        timeout);
    RpcRequestHolder.REQUEST_MAP.put(requestId, future);
    rpcConsumer.sendRequest(rpcProtocol, this.registryService);
    return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
  }
}
