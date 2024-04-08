package com.jiaz.request;

import static com.jiaz.command.Command.MESSAGE_REQUEST;

import com.jiaz.protocol.Packet;
import lombok.Data;

@Data
public class MessageRequestPacket extends Packet {

  private String message;

  public MessageRequestPacket(String message) {
    this.message = message;
  }

  @Override
  public Byte getCommand() {
    return MESSAGE_REQUEST;
  }

}
