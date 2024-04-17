package com.rpc.plus;

import java.io.Serializable;
import lombok.Data;

@Data
public class RpcRequest implements Serializable {

  private String serviceVersion;
  private String className;
  private String methodName;
  private Object[] params;
  private Class<?>[] parameterTypes;
}
