package com.rpc.plus.loadbalancer;

import com.rpc.plus.ServiceMeta;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.curator.x.discovery.ServiceInstance;

public class ZKConsistentHashLoadBalancer implements ServiceLoadBalancer<ServiceInstance<ServiceMeta>> {

  private final static int VIRTUAL_NODE_SIZE = 10;
  private final static String VIRTUAL_NODE_SPLIT = "#";

  @Override
  public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers,
      int hashCode) {
    TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = makeConsistentHashRing(servers);
    return allocateNode(ring, hashCode);
  }

  private ServiceInstance<ServiceMeta> allocateNode(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring, int hashCode) {
    Map.Entry<Integer, ServiceInstance<ServiceMeta>> entry = ring.ceilingEntry(hashCode);
    if (entry == null) {
      entry = ring.firstEntry();
    }
    return entry.getValue();
  }


  private TreeMap<Integer, ServiceInstance<ServiceMeta>> makeConsistentHashRing(List<ServiceInstance<ServiceMeta>> servers) {
    TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = new TreeMap<>();
    for (ServiceInstance<ServiceMeta> instance : servers) {
      for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
        ring.put((buildServiceInstanceKey(instance) + VIRTUAL_NODE_SPLIT + i).hashCode(), instance);
      }
    }
    return ring;
  }

  private String buildServiceInstanceKey(ServiceInstance<ServiceMeta> instance) {
    ServiceMeta payload = instance.getPayload();
    return String.join(":", payload.getServiceAddr(), String.valueOf(payload.getServicePort()));
  }
}