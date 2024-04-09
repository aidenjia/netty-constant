package com.jiaz.protocol.response;


import static com.jiaz.protocol.command.Command.MESSAGE_RESPONSE;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {

    private String fromUserId;

    private String fromUserName;

    private String message;

    @Override
    public Byte getCommand() {

        return MESSAGE_RESPONSE;
    }
}
