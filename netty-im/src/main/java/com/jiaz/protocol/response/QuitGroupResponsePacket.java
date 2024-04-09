package com.jiaz.protocol.response;


import lombok.Data;
import com.jiaz.protocol.Packet;
import static com.jiaz.protocol.command.Command.*;

@Data
public class QuitGroupResponsePacket extends Packet {

    private String groupId;

    private boolean success;

    private String reason;

    @Override
    public Byte getCommand() {

        return QUIT_GROUP_RESPONSE;
    }
}
