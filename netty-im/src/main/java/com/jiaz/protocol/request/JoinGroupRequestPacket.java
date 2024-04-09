package com.jiaz.protocol.request;

import static com.jiaz.protocol.command.Command.JOIN_GROUP_REQUEST;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class JoinGroupRequestPacket extends Packet {

    private String groupId;

    @Override
    public Byte getCommand() {

        return JOIN_GROUP_REQUEST;
    }
}
