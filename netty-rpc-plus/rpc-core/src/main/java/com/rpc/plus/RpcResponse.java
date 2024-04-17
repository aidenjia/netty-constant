package com.rpc.plus;

import java.io.Serializable;
import lombok.Data;

@Data
public class RpcResponse implements Serializable {
    private Object data;
    private String message;
}
