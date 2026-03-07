package com.medimate.backend.controller;

import com.medimate.backend.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final OnlineUserService onlineUserService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(java.time.LocalDateTime.now());
        // Private room এ message পাঠাও
        String room = chatMessage.getRoom() != null ? chatMessage.getRoom() : "public";
        messagingTemplate.convertAndSend("/topic/" + room, chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());

        // Online users এ add করো
        onlineUserService.userConnected(sessionId, chatMessage.getSenderName());

        chatMessage.setTimestamp(java.time.LocalDateTime.now());
        chatMessage.setType(ChatMessage.MessageType.JOIN);

        // সবাইকে জানাও নতুন user join করেছে
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
        messagingTemplate.convertAndSend("/topic/online-users",
                onlineUserService.getOnlineUsers());
    }

    @MessageMapping("/chat.getOnlineUsers")
    @SendTo("/topic/online-users")
    public java.util.Set<String> getOnlineUsers() {
        return onlineUserService.getOnlineUsers();
    }
}