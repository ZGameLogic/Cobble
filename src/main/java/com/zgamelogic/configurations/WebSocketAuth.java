package com.zgamelogic.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.authorization.WebsocketAuthData;
import com.zgamelogic.services.AuthService;
import com.zgamelogic.websocket.data.WebSocketAuthorization;
import com.zgamelogic.websocket.data.WebSocketMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.zgamelogic.data.Constants.DISCORD_ASPECT_ID;

@Component
@AllArgsConstructor
public class WebSocketAuth implements WebSocketAuthorization {
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void authenticate(WebSocketSession session) {
        WebSocketMessage message;
        if (session.getHandshakeHeaders().containsKey("code")) {
            String code = session.getHandshakeHeaders().get("code").get(0);
            WebsocketAuthData authData = authService.authorizeWithCode(code);
            session.getAttributes().put(DISCORD_ASPECT_ID, authData.userId());
            message = new WebSocketMessage("AUTHENTICATE", authData);
        } else if (session.getHandshakeHeaders().containsKey("token")) {
            String token = session.getHandshakeHeaders().get("token").get(0);
            WebsocketAuthData authData = authService.authorizeWithRollingToken(token);
            session.getAttributes().put(DISCORD_ASPECT_ID, authData.userId());
            message = new WebSocketMessage("AUTHENTICATE", authData);
        } else {
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
            session.sendMessage(textMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(WebSocketSession session) {

    }
}
