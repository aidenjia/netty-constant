package com.jiaz.client;

import com.jiaz.protocol.LoginRequestPacket;
import com.jiaz.protocol.Packet;
import com.jiaz.protocol.PacketCodeC;
import com.jiaz.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Date;
import java.util.UUID;

public class ClientHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    System.out.println(new Date() + ": 客户端开始登录");
    // 创建登录对象
    LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
    loginRequestPacket.setUserId(UUID.randomUUID().toString());
    loginRequestPacket.setUserName("flash");
    loginRequestPacket.setPassword("pwd");
    // 编码
    ByteBuf buffer = PacketCodeC.INSTANCE.encode(ctx.alloc().buffer(), loginRequestPacket);
    // 写数据
    ctx.channel().writeAndFlush(buffer);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf byteBuf = (ByteBuf) msg;
    Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);
    if (packet instanceof LoginResponsePacket) {
      LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;
      if (loginResponsePacket.isSuccess()) {
        System.out.println(new Date() + ": 客户端登录成功");
      } else {
        System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
      }
    }
  }

}
