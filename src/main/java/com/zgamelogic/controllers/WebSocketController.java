package com.zgamelogic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zgamelogic.data.authorization.WebsocketAuthData;
import com.zgamelogic.data.websocket.WebsocketMessage;
import com.zgamelogic.services.AuthService;
import com.zgamelogic.services.CobbleService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.zgamelogic.data.websocket.WebsocketMessageType.*;

@Service
public class WebSocketController extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions;
    private final ObjectMapper mapper;
    private final AuthService authService;
    private final CobbleService cobbleService;

    public WebSocketController(AuthService authService, CobbleService cobbleService) {
        super();
        this.authService = authService;
        sessions = new HashMap<>();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        this.cobbleService = cobbleService;
    }

    private void sendMessageToSession(String sessionId, WebsocketMessage objectMessage) {
        if(!sessions.containsKey(sessionId)) return;
        sendMessageToSession(sessions.get(sessionId), objectMessage);
    }

    private void sendMessageToSession(WebSocketSession session, WebsocketMessage objectMessage) {
        try {
            TextMessage message = new TextMessage(mapper.writeValueAsString(objectMessage));
            session.sendMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToUser(long discordId, WebsocketMessage objectMessage){
        sessions.values().stream().filter(session ->
            (long) session.getAttributes().get("Discord-ID") == discordId
        ).forEach(session -> sendMessageToSession(session.getId(), objectMessage));
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        try {
            if (session.getHandshakeHeaders().containsKey("code")) {
                String code = session.getHandshakeHeaders().get("code").get(0);
                WebsocketAuthData authData = authService.authorizeWithCode(code);
                session.getAttributes().put("Discord-ID", authData.userId());
                sessions.put(code, session);
                sendMessageToSession(code, new WebsocketMessage(AUTHENTICATION, authData));
            } else if (session.getHandshakeHeaders().containsKey("token")) {
                String token = session.getHandshakeHeaders().get("token").get(0);
                WebsocketAuthData authData = authService.authorizeWithRollingToken(token);
                session.getAttributes().put("Discord-ID", authData.userId());
                sessions.put(token, session);
                sendMessageToSession(token, new WebsocketMessage(AUTHENTICATION, authData));
            } else {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
        } catch (Exception e) {
            session.sendMessage(new TextMessage(e.getMessage()));
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
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
        WebsocketMessage webmsg = mapper.readValue(payload, WebsocketMessage.class);
        try {
            switch (webmsg.getType()) {
                case INITIAL_DATA -> {
                    sendMessageToSession(session, new WebsocketMessage(
                        INITIAL_DATA,
                        webmsg.getReplyId(),
                        cobbleService.getCobblePlayer((Long) session.getAttributes().get("Discord-ID"))
                    ));
                }
            }
        } catch(Exception e){
            sendMessageToSession(session, new WebsocketMessage(ERROR, webmsg.getReplyId(), e.getMessage()));
        }
    }
}
