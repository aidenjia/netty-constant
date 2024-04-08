package com.jiaz.server.handler;

import com.jiaz.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class Spliter extends LengthFieldBasedFrameDecoder {

  private static final int LENGTH_FIELD_OFFSET = 7;
  private static final int LENGTH_FIELD_LENGTH = 4;

  /**
   * 第一个参数指的是数据包的最大长度，
   * 第二个参数指的是长度域的偏移量，
   * 第三个参数指的是长度域的长度，
   * 这样一个拆包器写好之后，只需要在 pipeline 的最前面加上这个拆包器
   */
  public Spliter() {
    super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    // 屏蔽非本协议的客户端
    if (in.getInt(in.readerIndex()) != PacketCodeC.MAGIC_NUMBER) {
      System.out.println("非本协议，拒绝连接");
      ctx.channel().close();
      return null;
    }

    return super.decode(ctx, in);
  }

}
