package com.jiaz.client.console;

import com.jiaz.protocol.request.LogoutRequestPacket;
import io.netty.channel.Channel;
import java.util.Scanner;

public class LogoutConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        LogoutRequestPacket logoutRequestPacket = new LogoutRequestPacket();
        channel.writeAndFlush(logoutRequestPacket);
    }
}
