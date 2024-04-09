package com.jiaz.protocol.request;



import static com.jiaz.protocol.command.Command.CREATE_GROUP_REQUEST;

import com.jiaz.protocol.Packet;
import java.util.List;
import lombok.Data;

@Data
public class CreateGroupRequestPacket extends Packet {

    private List<String> userIdList;

    @Override
    public Byte getCommand() {

        return CREATE_GROUP_REQUEST;
    }
}
