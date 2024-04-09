package com.jiaz.protocol.response;

import com.jiaz.protocol.Packet;
import static com.jiaz.protocol.command.Command.*;
public class HeartBeatResponsePacket extends Packet {
    @Override
    public Byte getCommand() {
        return HEARTBEAT_RESPONSE;
    }
}
