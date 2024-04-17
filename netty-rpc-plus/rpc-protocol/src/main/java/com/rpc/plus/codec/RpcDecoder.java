package com.rpc.plus.codec;

import com.rpc.plus.RpcRequest;
import com.rpc.plus.RpcResponse;
import com.rpc.plus.protocol.MsgHeader;
import com.rpc.plus.protocol.MsgType;
import com.rpc.plus.protocol.ProtocolConstants;
import com.rpc.plus.protocol.RpcProtocol;
import com.rpc.plus.serialization.RpcSerialization;
import com.rpc.plus.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

  /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
    */

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
      return;
    }
    in.markReaderIndex();
    short magic = in.readShort();
    if (magic != ProtocolConstants.MAGIC) {
      throw new IllegalArgumentException("magic number is illegal, " + magic);
    }
    byte version = in.readByte();
    byte serializeType = in.readByte();
    byte msgType = in.readByte();
    byte status = in.readByte();
    long requestId = in.readLong();
    int dataLen = in.readInt();
    if (in.readableBytes() < dataLen) {
      in.resetReaderIndex();
      return;
    }
    byte[] data = new byte[dataLen];
    in.readBytes(data);
    MsgType msgTypeEnum = MsgType.findByType(msgType);
    if (msgTypeEnum == null) {
      return;
    }
    MsgHeader header = new MsgHeader();
    header.setMagic(magic);
    header.setVersion(version);
    header.setSerialization(serializeType);
    header.setStatus(status);
    header.setStatus(status);
    header.setRequestId(requestId);
    header.setMsgLen(dataLen);
    header.setMsgType(msgType);
    RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(serializeType);
    switch (msgTypeEnum) {
      case REQUEST:
        RpcRequest request = rpcSerialization.deserialize(data, RpcRequest.class);
        if (request != null) {
          RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
          protocol.setHeader(header);
          protocol.setBody(request);
          out.add(protocol);
        }
        break;
      case RESPONSE:
        RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
        if (response != null) {
          RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
          protocol.setHeader(header);
          protocol.setBody(response);
          out.add(protocol);
        }
        break;
      case HEARTBEAT:
        // TODO
        break;
    }
  }
}
