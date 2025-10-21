package com.benefitj.wschat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMessage {

  private MessageType type;
  private String content;
  private String sender;
  private String room;
  private Date timestamp;

  public ChatMessage(MessageType type, String content, String sender, String room) {
    this.type = type;
    this.content = content;
    this.sender = sender;
    this.room = room;
    this.timestamp = new Date();
  }

  public enum MessageType {
    JOIN, CHAT, LEAVE
  }

}
