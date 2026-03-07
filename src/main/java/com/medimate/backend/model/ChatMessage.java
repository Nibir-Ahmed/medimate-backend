package com.medimate.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private String senderName;
    private String receiverName;
    private String message;
    private String room;
    private LocalDateTime timestamp;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}