package com.jiaz.protocol.response;


import static com.jiaz.protocol.command.Command.LOGIN_RESPONSE;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class LoginResponsePacket extends Packet {
    private String userId;

    private String userName;

    private boolean success;

    private String reason;


    @Override
    public Byte getCommand() {

        return LOGIN_RESPONSE;
    }
}
