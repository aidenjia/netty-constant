package com.jiaz.protocol.response;

import static com.jiaz.protocol.command.Command.CREATE_GROUP_RESPONSE;

import com.jiaz.protocol.Packet;
import java.util.List;
import lombok.Data;

@Data
public class CreateGroupResponsePacket extends Packet {
    private boolean success;

    private String groupId;

    private List<String> userNameList;

    @Override
    public Byte getCommand() {

        return CREATE_GROUP_RESPONSE;
    }
}
