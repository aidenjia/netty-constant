package com.jiaz.protocol.request;


import static com.jiaz.protocol.command.Command.QUIT_GROUP_REQUEST;

import lombok.Data;
import com.jiaz.protocol.Packet;
@Data
public class QuitGroupRequestPacket extends Packet {

    private String groupId;

    @Override
    public Byte getCommand() {

        return QUIT_GROUP_REQUEST;
    }
}
