package com.jiaz.protocol.request;



import lombok.Data;
import lombok.NoArgsConstructor;
import com.jiaz.protocol.Packet;
import static com.jiaz.protocol.command.Command.*;
@Data
@NoArgsConstructor
public class GroupMessageRequestPacket extends Packet {
    private String toGroupId;
    private String message;

    public GroupMessageRequestPacket(String toGroupId, String message) {
        this.toGroupId = toGroupId;
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        return GROUP_MESSAGE_REQUEST;
    }
}
