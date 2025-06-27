package com.zgamelogic.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WebSocketService extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions;
    private final ObjectMapper mapper;

    public WebSocketService() {
        super();
        sessions = new HashMap<>();
        mapper = new ObjectMapper();
    }

    public void sendMessage(long discordId, Object objectMessage){
        try {
            TextMessage message = new TextMessage(mapper.writeValueAsString(objectMessage));
            sessions.values().stream().filter(session ->
                Long.parseLong(session.getHandshakeHeaders().get("Discord-ID").get(0)) == discordId
            ).forEach(session -> {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);
        session.sendMessage(new TextMessage("Hello " + payload + "!"));
    }
}
