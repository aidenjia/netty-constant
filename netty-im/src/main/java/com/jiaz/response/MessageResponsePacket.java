package com.jiaz.response;


import static com.jiaz.command.Command.MESSAGE_RESPONSE;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class MessageResponsePacket extends Packet {

  private String message;

  @Override
  public Byte getCommand() {
    return MESSAGE_RESPONSE;
  }
}
