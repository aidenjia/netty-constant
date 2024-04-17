package com.rpc.plus;

import java.io.IOException;

public interface RegistryService {

  void register(ServiceMeta serviceMeta) throws Exception;

  void unRegister(ServiceMeta serviceMeta) throws Exception;

  ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

  void destroy() throws IOException;
}
