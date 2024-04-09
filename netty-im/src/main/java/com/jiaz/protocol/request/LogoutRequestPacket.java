package com.jiaz.protocol.request;



import static com.jiaz.protocol.command.Command.LOGOUT_REQUEST;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class LogoutRequestPacket extends Packet {
    @Override
    public Byte getCommand() {

        return LOGOUT_REQUEST;
    }
}
