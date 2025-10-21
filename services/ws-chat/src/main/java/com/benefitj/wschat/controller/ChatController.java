package com.benefitj.wschat.controller;

import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.wschat.message.ChatMessage;
import io.swagger.annotations.Api;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Api("聊天")
@AopWebPointCut
@Controller
public class ChatController {

  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    return chatMessage;
  }

  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public ChatMessage addUser(@Payload ChatMessage chatMessage,
                             SimpMessageHeaderAccessor headerAccessor) {
    // 将用户名添加到 WebSocket session
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    return chatMessage;
  }
}