package com.jiaz.protocol.response;



import static com.jiaz.protocol.command.Command.GROUP_MESSAGE_RESPONSE;

import com.jiaz.protocol.Packet;
import com.jiaz.session.Session;
import lombok.Data;


@Data
public class GroupMessageResponsePacket extends Packet {

    private String fromGroupId;

    private Session fromUser;

    private String message;

    @Override
    public Byte getCommand() {

        return GROUP_MESSAGE_RESPONSE;
    }
}
