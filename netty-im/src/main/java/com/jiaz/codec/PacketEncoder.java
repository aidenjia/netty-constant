package com.jiaz.codec;

import com.jiaz.protocol.Packet;
import com.jiaz.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out)  {
    //将msg写入ByteBuf
    PacketCodeC.INSTANCE.encode(out,msg);
  }
}
