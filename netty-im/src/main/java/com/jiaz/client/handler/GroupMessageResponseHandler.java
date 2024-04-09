package com.jiaz.client.handler;

import com.jiaz.protocol.response.GroupMessageResponsePacket;
import com.jiaz.session.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GroupMessageResponseHandler extends
    SimpleChannelInboundHandler<GroupMessageResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMessageResponsePacket responsePacket) {
        String fromGroupId = responsePacket.getFromGroupId();
        Session fromUser = responsePacket.getFromUser();
        System.out.println("收到群[" + fromGroupId + "]中[" + fromUser + "]发来的消息：" + responsePacket.getMessage());
    }
}
