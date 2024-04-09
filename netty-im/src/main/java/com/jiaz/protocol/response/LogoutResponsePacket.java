package com.jiaz.protocol.response;


import static com.jiaz.protocol.command.Command.LOGOUT_RESPONSE;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class LogoutResponsePacket extends Packet {

    private boolean success;

    private String reason;


    @Override
    public Byte getCommand() {

        return LOGOUT_RESPONSE;
    }
}
