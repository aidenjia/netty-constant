package com.jiaz.transport;

import com.jiaz.protocol.Message;
import com.jiaz.protocol.Request;
import com.jiaz.protocol.Response;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Connection implements Closeable {

  // 用于生成消息ID，全局唯一
  private final static AtomicLong ID_GENERATOR = new AtomicLong(0);

  // TODO 时间轮定时删除
  public final static Map<Long, NettyResponseFuture<Response>> IN_FLIGHT_REQUEST_MAP
      = new ConcurrentHashMap<>();

  private ChannelFuture future;

  private AtomicBoolean isConnected = new AtomicBoolean();

  public Connection() {
    this.isConnected.set(false);
    this.future = null;
  }

  public Connection(ChannelFuture future, boolean isConnected) {
    this.future = future;
    this.isConnected.set(isConnected);
  }

  public ChannelFuture getFuture() {
    return future;
  }

  public void setFuture(ChannelFuture future) {
    this.future = future;
  }

  public AtomicBoolean getIsConnected() {
    return isConnected;
  }

  public void setIsConnected(AtomicBoolean isConnected) {
    this.isConnected = isConnected;
  }

  public NettyResponseFuture<Response> request(Message<Request> message, long timeOut) {
    long messageId = ID_GENERATOR.incrementAndGet();
    message.getHeader().setMessageId(messageId);
    NettyResponseFuture responseFuture = new NettyResponseFuture<>(System.currentTimeMillis(),
        timeOut, message, future.channel(), new DefaultPromise(new DefaultEventLoop()));
    // 将消息ID和关联的Future记录到IN_FLIGHT_REQUEST_MAP集合中
    IN_FLIGHT_REQUEST_MAP.put(messageId, responseFuture);
    try {
      //发送请求
      future.channel().writeAndFlush(message);
    } catch (Exception e) {
      // 发送请求异常时，删除对应的Future
      IN_FLIGHT_REQUEST_MAP.remove(messageId);
      throw e;
    }
    return responseFuture;
  }

  @Override
  public void close() throws IOException {
    future.channel().close();
  }
}
