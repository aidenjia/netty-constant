package com.jiaz.server;

import com.jiaz.protocol.LoginRequestPacket;
import com.jiaz.protocol.Packet;
import com.jiaz.protocol.PacketCodeC;
import com.jiaz.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Date;

public class ServerHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    System.out.println(new Date() + ": 客户端开始登录……");
    ByteBuf requestByteBuf = (ByteBuf) msg;
    // 解码
    Packet packet = PacketCodeC.INSTANCE.decode(requestByteBuf);
    // 判断是否是登录请求数据包
    if (packet instanceof LoginRequestPacket) {
      LoginResponsePacket loginResponsePacket = getLoginResponsePacket(packet);
      // 编码
      ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc().buffer(), loginResponsePacket);
      ctx.channel().writeAndFlush(responseByteBuf);
    }

  }

  private LoginResponsePacket getLoginResponsePacket(Packet packet) {
    LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
    // 登录校验
    if (valid(loginRequestPacket)) {
      // 校验成功
    } else {
      // 校验失败
    }
    LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
    loginResponsePacket.setVersion(packet.getVersion());
    if (valid(loginRequestPacket)) {
      loginResponsePacket.setSuccess(true);
    } else {
      loginResponsePacket.setReason("账号密码校验失败");
      loginResponsePacket.setSuccess(false);
    }
    return loginResponsePacket;
  }

  private boolean valid(LoginRequestPacket loginRequestPacket) {
    return true;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
      throws Exception {
    System.out.println("error"+cause.getCause());
  }
}
