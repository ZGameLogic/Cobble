package com.zgamelogic.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.CobbleServiceException;
import com.zgamelogic.data.authorization.WebsocketAuthData;
import com.zgamelogic.data.player.Player;
import com.zgamelogic.data.websocket.WebsocketMessage;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.zgamelogic.data.websocket.WebsocketMessageType.AUTHENTICATION;
import static com.zgamelogic.data.websocket.WebsocketMessageType.INITIAL_DATA;

@Service
public class CobbleWebsocketService {
    private final Map<String, WebSocketSession> sessions;
    private final ObjectMapper mapper;
    private final AuthService authService;
    private final CobbleService cobbleService;

    public CobbleWebsocketService(ObjectMapper mapper, AuthService authService, CobbleService cobbleService) {
        sessions = new HashMap<>();
        this.mapper = mapper;
        this.authService = authService;
        this.cobbleService = cobbleService;
    }

    public void sendMessageToUser(long discordId, WebsocketMessage objectMessage){
        sessions.values().stream().filter(session ->
            (long) session.getAttributes().get("Discord-ID") == discordId
        ).forEach(session -> sendMessageToSession(session.getId(), objectMessage));
    }

    public void connectionEstablished(WebSocketSession session) throws IOException {
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

    public void connectionClosed(WebSocketSession session) {
        if(session.getHandshakeHeaders().containsKey("code")){
            sessions.remove(session.getHandshakeHeaders().get("code").get(0));
        } else if(session.getHandshakeHeaders().containsKey("token")){
            sessions.remove(session.getHandshakeHeaders().get("token").get(0));
        }
    }

    private void sendMessageToSession(String sessionId, WebsocketMessage objectMessage) {
        if(!sessions.containsKey(sessionId)) return;
        sendMessageToSession(sessions.get(sessionId), objectMessage);
    }

    public void sendMessageToSession(WebSocketSession session, WebsocketMessage objectMessage) {
        try {
            TextMessage message = new TextMessage(mapper.writeValueAsString(objectMessage));
            session.sendMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void initialData(WebSocketSession session, WebsocketMessage webmsg) throws CobbleServiceException {
        Player player = cobbleService.getCobblePlayer((Long) session.getAttributes().get("Discord-ID"));
        Hibernate.initialize(player.getBuildings());
        sendMessageToSession(session, new WebsocketMessage(
            INITIAL_DATA,
            webmsg.getReplyId(),
            player
        ));
    }
}
