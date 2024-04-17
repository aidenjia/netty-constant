package com.rpc.plus.protocol;

import java.io.Serializable;
import lombok.Data;

@Data
public class RpcProtocol<T> implements Serializable {
    private MsgHeader header;
    private T body;
}
