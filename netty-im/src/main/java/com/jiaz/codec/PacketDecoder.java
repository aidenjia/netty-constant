package com.jiaz.codec;

import com.jiaz.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    //将ByteBuf通过序列化算法解码成相应的Object,避免了重复的代码的书写
    out.add(PacketCodeC.INSTANCE.decode(in));
  }
}
