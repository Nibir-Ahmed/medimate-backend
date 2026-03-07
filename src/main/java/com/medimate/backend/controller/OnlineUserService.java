package com.medimate.backend.controller;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserService {

    // sessionId → userName
    private final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    public void userConnected(String sessionId, String userName) {
        onlineUsers.put(sessionId, userName);
    }

    public void userDisconnected(String sessionId) {
        onlineUsers.remove(sessionId);
    }

    public Set<String> getOnlineUsers() {
        return Set.copyOf(onlineUsers.values());
    }
}