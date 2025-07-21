package com.zgamelogic.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.authorization.WebsocketAuthData;
import com.zgamelogic.services.AuthService;
import com.zgamelogic.websocket.data.WebSocketAuthorization;
import com.zgamelogic.websocket.data.WebSocketMessage;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;

import static com.zgamelogic.data.Constants.DISCORD_ASPECT_ID;
import static com.zgamelogic.data.Constants.STATE_ASPECT_ID;

@Component
@AllArgsConstructor
public class WebSocketAuth implements WebSocketAuthorization {
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final TaskScheduler taskScheduler;

    @Override
    public void authenticate(WebSocketSession session) {
        if (session.getHandshakeHeaders().containsKey("state")) {
            String state = session.getHandshakeHeaders().get("state").get(0);
            session.getAttributes().put(STATE_ASPECT_ID, state);
            taskScheduler.schedule(
                () -> checkAuth(session),
                Instant.now().plusSeconds(5 * 60)
            );
        } else if (session.getHandshakeHeaders().containsKey("token")) {
            String token = session.getHandshakeHeaders().get("token").get(0);
            WebsocketAuthData authData = authService.authorizeWithRollingToken(token);
            session.getAttributes().put(DISCORD_ASPECT_ID, authData.userId());
            WebSocketMessage message = new WebSocketMessage("AUTHENTICATE", authData);
            try {
                TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
                session.sendMessage(textMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void logout(WebSocketSession session) {

    }

    @Override
    public boolean authorizedSession(WebSocketSession session) {
        return session.getAttributes().containsKey(DISCORD_ASPECT_ID);
    }

    private void checkAuth(WebSocketSession session){
        if(session.isOpen() && !session.getAttributes().containsKey(DISCORD_ASPECT_ID)){
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
