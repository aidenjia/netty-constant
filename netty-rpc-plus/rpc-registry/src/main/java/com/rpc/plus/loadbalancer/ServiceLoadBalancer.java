package com.rpc.plus.loadbalancer;

import java.util.List;

public interface ServiceLoadBalancer<T> {
  T select(List<T> servers, int hashCode);
}
