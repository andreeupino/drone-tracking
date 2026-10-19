package com.asdt.backend.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession s) {
        sessions.put(s.getId(), new ConcurrentWebSocketSessionDecorator(s, 1000, 64 * 1024));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession s, CloseStatus status) {
        sessions.remove(s.getId());
    }

    public void broadcast(String json) {
        TextMessage msg = new TextMessage(json);
        sessions.values().forEach(s -> {
            try { if (s.isOpen()) s.sendMessage(msg); } catch (Exception ignored) {}
        });
    }
}