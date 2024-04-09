package com.jiaz.protocol.response;

import static com.jiaz.protocol.command.Command.JOIN_GROUP_RESPONSE;

import lombok.Data;
import com.jiaz.protocol.Packet;

@Data
public class JoinGroupResponsePacket extends Packet {
    private String groupId;

    private boolean success;

    private String reason;

    @Override
    public Byte getCommand() {

        return JOIN_GROUP_RESPONSE;
    }
}
