package com.zgamelogic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.websocket.WebsocketMessage;
import com.zgamelogic.services.CobbleWebsocketService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static com.zgamelogic.data.websocket.WebsocketMessageType.*;

@Service
@AllArgsConstructor
public class WebSocketController extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final CobbleWebsocketService cobbleWebsocketService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        cobbleWebsocketService.connectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        cobbleWebsocketService.connectionClosed(session);
    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        WebsocketMessage webmsg = mapper.readValue(payload, WebsocketMessage.class);
        try {
            switch (webmsg.getType()) {
                case INITIAL_DATA -> {
                    cobbleWebsocketService.initialData(session, webmsg);
                }
            }
        } catch(Exception e){
            cobbleWebsocketService.sendMessageToSession(session, new WebsocketMessage(ERROR, webmsg.getReplyId(), e.getMessage()));
        }
    }
}
