package com.zgamelogic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketController extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions;
    private final ObjectMapper mapper;

    public WebSocketController() {
        super();
        sessions = new HashMap<>();
        mapper = new ObjectMapper();
    }

    public void sendMessageToSession(String sessionId, Object objectMessage) {
        if(!sessions.containsKey(sessionId)) return;
        try {
            TextMessage message = new TextMessage(mapper.writeValueAsString(objectMessage));
            sessions.get(sessionId).sendMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToUser(long discordId, Object objectMessage){
        sessions.values().stream().filter(session ->
            (long) session.getAttributes().get("Discord-ID") == discordId
        ).forEach(session -> sendMessageToSession(session.getId(), objectMessage));
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        if(session.getHandshakeHeaders().containsKey("code")){
            // TODO authenticate with code
            // TODO set attribute Discord-ID (long) to discord id
            sessions.put(session.getHandshakeHeaders().get("code").get(0), session);
        } else if(session.getHandshakeHeaders().containsKey("token")){
            // TODO authenticate with token
            // TODO set attribute Discord-ID (long) to discord id
            sessions.put(session.getHandshakeHeaders().get("token").get(0), session);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        // TODO update rolling token
        // TODO send Rolling Token, User id, Avatar, Username
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        if(session.getHandshakeHeaders().containsKey("code")){
            sessions.remove(session.getHandshakeHeaders().get("code").get(0));
        } else if(session.getHandshakeHeaders().containsKey("token")){
            sessions.remove(session.getHandshakeHeaders().get("token").get(0));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);
        session.sendMessage(new TextMessage("Hello " + payload + "!"));
    }
}
