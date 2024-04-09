package com.jiaz.protocol.response;


import com.jiaz.session.Session;
import java.util.List;
import lombok.Data;
import com.jiaz.protocol.Packet;
import static com.jiaz.protocol.command.Command.*;

@Data
public class ListGroupMembersResponsePacket extends Packet {

    private String groupId;

    private List<Session> sessionList;

    @Override
    public Byte getCommand() {

        return LIST_GROUP_MEMBERS_RESPONSE;
    }
}
