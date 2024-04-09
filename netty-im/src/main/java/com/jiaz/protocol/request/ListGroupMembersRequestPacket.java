package com.jiaz.protocol.request;

import com.jiaz.protocol.Packet;
import static com.jiaz.protocol.command.Command.*;

import lombok.Data;

@Data
public class ListGroupMembersRequestPacket extends Packet {

    private String groupId;

    @Override
    public Byte getCommand() {

        return LIST_GROUP_MEMBERS_REQUEST;
    }
}
