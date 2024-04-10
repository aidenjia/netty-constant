package com.jiaz.timer_task;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCleanTimeTask implements TimerTask {

  private final Long tick;

  protected volatile boolean cancel = false;

  public AbstractCleanTimeTask(Long tick) {
    this.tick = tick;
  }

  @Override
  public void run(Timeout timeout) throws Exception {
    doClean();
    reput(timeout, tick);
  }

  public abstract void doClean();

  private void reput(Timeout timeout, Long tick) {
    if (timeout == null || tick == null) {
      throw new IllegalArgumentException();
    }
    Timer timer = timeout.timer();
    if (cancel) {
      return;
    }
    if (timeout.isCancelled()) {
      return;
    }
    timer.newTimeout(timeout.task(), tick, TimeUnit.MILLISECONDS);
  }

}
