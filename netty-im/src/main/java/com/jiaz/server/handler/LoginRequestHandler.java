package com.jiaz.server.handler;

import com.jiaz.protocol.LoginRequestPacket;
import com.jiaz.response.LoginResponsePacket;
import com.jiaz.session.Session;
import com.jiaz.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Date;
import java.util.UUID;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket loginRequestPacket){
    System.out.println(new Date() + ": 收到客户端登录请求……");

    LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
    loginResponsePacket.setVersion(loginRequestPacket.getVersion());
    if (valid(loginRequestPacket)) {
      loginResponsePacket.setSuccess(true);
      String userId = randomUserId();
      loginResponsePacket.setUserId(userId);
      System.out.println("[" + loginRequestPacket.getUserName() + "]登录成功");
      SessionUtil.bindSession(new Session(userId, loginRequestPacket.getUserName()), ctx.channel());
    } else {
      loginResponsePacket.setReason("账号密码校验失败");
      loginResponsePacket.setSuccess(false);
      System.out.println(new Date() + ": 登录失败!");
    }
    // 登录响应
    ctx.channel().writeAndFlush(loginResponsePacket);
  }

  private static String randomUserId() {
    return UUID.randomUUID().toString().split("-")[0];
  }
  private boolean valid(LoginRequestPacket loginRequestPacket) {
    return true;
  }
}
