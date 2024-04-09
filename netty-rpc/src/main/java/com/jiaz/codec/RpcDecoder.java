package com.jiaz.codec;

import com.jiaz.compress.Compressor;
import com.jiaz.compress.CompressorFactory;
import com.jiaz.constants.Constants;
import com.jiaz.protocol.Header;
import com.jiaz.protocol.Message;
import com.jiaz.protocol.Request;
import com.jiaz.protocol.Response;
import com.jiaz.serialization.Serialization;
import com.jiaz.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import java.util.logging.Handler;

public class RpcDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out)
      throws Exception {
    if (byteBuf.readableBytes() < Constants.HEADER_SIZE) {
      return; // 不到16字节的话无法解析消息头，暂不读取
    }
    //记录当前readIndex指针的位置，方便重置
    byteBuf.markReaderIndex();
    short magic = byteBuf.readShort();
    if (magic != Constants.MAGIC) {
      byteBuf.resetReaderIndex(); // 重置readIndex指针
      throw new RuntimeException("magic number error:" + magic);
    }
    // 依次读取消息版本、附加信息、消息ID以及消息体长度四部分
    byte version = byteBuf.readByte();
    byte extraInfo = byteBuf.readByte();
    long messageId = byteBuf.readLong();
    int size = byteBuf.readInt();
    //心跳消息是没有消息体的
    Object body = null;
    if (!Constants.isHeartBeat(extraInfo)) {
      if (byteBuf.readableBytes() < size) {
        byteBuf.resetReaderIndex();
        return;
      }
      // 读取消息体并进行反序列化
      byte[] payload = new byte[size];
      byteBuf.readBytes(payload);
      // 这里根据消息头中的extraInfo部分选择相应的序列化和压缩方式
      Serialization serialization = SerializationFactory.get(extraInfo);
      Compressor compressor = CompressorFactory.get(extraInfo);
      if (Constants.isRequest(extraInfo)) {
        body = serialization.deserialize(compressor.unCompress(payload), Request.class);
      } else {
        body = serialization.deserialize(compressor.unCompress(payload), Response.class);
      }
      Header header = new Header(magic, version, extraInfo, messageId, size);
      Message message = new Message<>(header, body);
      out.add(message);
    }
  }
}
