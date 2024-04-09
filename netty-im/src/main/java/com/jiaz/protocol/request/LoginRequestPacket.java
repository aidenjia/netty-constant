package com.jiaz.protocol.request;


import static com.jiaz.protocol.command.Command.LOGIN_REQUEST;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class LoginRequestPacket extends Packet {
    private String userName;

    private String password;

    @Override
    public Byte getCommand() {

        return LOGIN_REQUEST;
    }
}
