package com.jiaz.embedded_channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jiaz.embeddedChannel.FixedLengthFrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

public class FixedLengthFrameDecoderTest {


  @Test
  public void testFramesDecoded() {
    //创建一个 ByteBuf，并存储 9 字节
    ByteBuf buf = Unpooled.buffer();
    for (int i = 0; i < 9; i++) {
      buf.writeByte(i);
    }
    ByteBuf input = buf.duplicate();
    //创建一个EmbeddedChannel，并添加一个FixedLengthFrameDecoder，其将以 3 字节的帧长度被测试
    EmbeddedChannel channel = new EmbeddedChannel(
        new FixedLengthFrameDecoder(3));
    // write bytes
    //将数据写入EmbeddedChannel
    assertTrue(channel.writeInbound(input.retain()));
    //标记 Channel 为已完成状态
    assertTrue(channel.finish());

    // read messages
    //读取所生成的消息，并且验证是否有 3 帧（切片），其中每帧（切片）都为 3 字节
    ByteBuf read = (ByteBuf) channel.readInbound();
    assertEquals(buf.readSlice(3), read);
    read.release();

    read = (ByteBuf) channel.readInbound();
    assertEquals(buf.readSlice(3), read);
    read.release();

    read = (ByteBuf) channel.readInbound();
    assertEquals(buf.readSlice(3), read);
    read.release();

    assertNull(channel.readInbound());
    buf.release();
  }

}
