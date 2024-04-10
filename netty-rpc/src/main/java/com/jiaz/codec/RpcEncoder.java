package com.jiaz.codec;

import com.jiaz.compress.Compressor;
import com.jiaz.compress.CompressorFactory;
import com.jiaz.constants.Constants;
import com.jiaz.protocol.Header;
import com.jiaz.protocol.Message;
import com.jiaz.serialization.Serialization;
import com.jiaz.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<Message> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf byteBuf) throws Exception {
    Header header = message.getHeader();
    byteBuf.writeShort(header.getMagic());
    byteBuf.writeByte(header.getVersion());
    byteBuf.writeByte(header.getExtraInfo());
    byteBuf.writeLong(header.getMessageId());
    Object content = message.getContent();
    if(Constants.isHeartBeat(header.getExtraInfo())){
      byteBuf.writeInt(0);
      return;
    }
    Serialization serialization = SerializationFactory.get(header.getExtraInfo());
    Compressor compressor = CompressorFactory.get(header.getExtraInfo());
    byte[] payload = compressor.compress(serialization.serialize(content));
    byteBuf.writeInt(payload.length);//写入消息体长度
    byteBuf.writeBytes(payload);// 写入消息体
  }
}
