package com.jiaz.proxy;

import static com.jiaz.constants.Constants.MAGIC;
import static com.jiaz.constants.Constants.VERSION_1;

import com.jiaz.constants.Constants;
import com.jiaz.protocol.Header;
import com.jiaz.protocol.Message;
import com.jiaz.protocol.Request;
import com.jiaz.registry.Registry;
import com.jiaz.registry.ServerInfo;
import com.jiaz.transport.Connection;
import com.jiaz.transport.NettyClient;
import com.jiaz.transport.NettyResponseFuture;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.curator.x.discovery.ServiceInstance;

public class RpcProxy implements InvocationHandler {

  private String serviceName; // 需要代理的服务(接口)名称

  public Map<Method, Header> headerCache = new ConcurrentHashMap<>();

  // 用于与Zookeeper交互，其中自带缓存
  private Registry<ServerInfo> registry;

  public RpcProxy(String serviceName,
      Registry<ServerInfo> registry) {
    this.serviceName = serviceName;
    this.registry = registry;
  }

  public static <T> T newInstance(Class<T> clazz, Registry<ServerInfo> registry) throws Exception {
    // 创建代理对象
    return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
        new Class[]{clazz},
        new RpcProxy("demoService", registry));
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 从Zookeeper缓存中获取可用的Server地址,并随机从中选择一个
    List<ServiceInstance<ServerInfo>> serviceInstances =
        registry.queryForInstances(serviceName);
    ServiceInstance<ServerInfo> serviceInstance =
        serviceInstances.get(ThreadLocalRandom.current().nextInt(serviceInstances.size()));
    String methodName = method.getName();
    Header header = headerCache.computeIfAbsent(method, h -> new Header(MAGIC, VERSION_1));
    Message<Request> message = new Message(header, new Request(serviceName, methodName, args));
    return remoteCall(serviceInstance.getPayload(), message);
  }

  protected Object remoteCall(ServerInfo serverInfo, Message message) {
    if (serverInfo == null) {
      throw new RuntimeException("get available server error");
    }
    NettyClient nettyClient = new NettyClient(serverInfo.getHost(), serverInfo.getPort());
    ChannelFuture channelFuture = nettyClient.connect().awaitUninterruptibly();
    Connection connection = new Connection(channelFuture, true);
    NettyResponseFuture responseFuture = connection.request(message, Constants.DEFAULT_TIMEOUT);
    // 等待请求对应的响应
    Object result;
    try {
      result = responseFuture.getPromise().get(Constants.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    } catch (TimeoutException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
