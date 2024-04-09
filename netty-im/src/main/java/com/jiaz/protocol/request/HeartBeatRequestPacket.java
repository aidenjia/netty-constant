package com.jiaz.protocol.request;

import com.jiaz.protocol.Packet;
import lombok.Data;

import static com.jiaz.protocol.command.Command.*;

@Data
public class HeartBeatRequestPacket extends Packet {

  private String message;

//  public HeartBeatRequestPacket(String message) {
//    this.message = message;
//  }

  @Override
  public Byte getCommand() {
    return HEARTBEAT_REQUEST;
  }
}
