package com.jiaz.timer_task;

import com.jiaz.protocol.Response;
import com.jiaz.transport.NettyResponseFuture;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ResponseFutureMapCleanTask extends AbstractCleanTimeTask {

  private final Map<Long, NettyResponseFuture<Response>> responseFutureMap;

  public ResponseFutureMapCleanTask(Map<Long, NettyResponseFuture<Response>> responseFutureMap,
      long cleanTime) {
    super(cleanTime);
    this.responseFutureMap = responseFutureMap;
  }


  @Override
  public void doClean() {
    System.out.println("时间轮开始执行, responseFutureMap size: " + responseFutureMap.size() + ":"
        + Thread.currentThread().getName());
    Iterator<Entry<Long, NettyResponseFuture<Response>>> iterator = responseFutureMap.entrySet()
        .iterator();
    while (iterator.hasNext()) {
      NettyResponseFuture<Response> future = iterator.next().getValue();
      long createTimestamp = future.getCreateTime();
      long now = System.currentTimeMillis();
      if (now - createTimestamp > future.getTimeOut()) {
        iterator.remove();
        System.out.println("future被删除,messageId");
      }
    }
    if (!iterator.hasNext()) {
      cancel = true;
    }
  }
}
