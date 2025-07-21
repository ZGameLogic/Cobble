package com.zgamelogic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.CobbleServiceException;
import com.zgamelogic.data.authorization.WebsocketAuthData;
import com.zgamelogic.services.AuthService;
import com.zgamelogic.websocket.components.WebSocketService;
import com.zgamelogic.websocket.data.WebSocketMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.zgamelogic.data.Constants.DISCORD_ASPECT_ID;
import static com.zgamelogic.data.Constants.STATE_ASPECT_ID;

@Controller
@AllArgsConstructor
public class CobbleController {
    private final WebSocketService webSocketService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @GetMapping("/register")
    public String register(@RequestParam String code, @RequestParam String state) throws CobbleServiceException {
        WebSocketSession session = webSocketService.getSessions().values().stream()
            .filter(s -> s.getAttributes().getOrDefault(STATE_ASPECT_ID, "").equals(state))
            .findFirst().orElseThrow(() -> new CobbleServiceException("Unable to find session with provided state. Please try again."));
        WebsocketAuthData authData = authService.authorizeWithCode(code);
        session.getAttributes().remove(state);
        session.getAttributes().put(DISCORD_ASPECT_ID, authData.userId());
        try {
            WebSocketMessage message = new WebSocketMessage("AUTHENTICATE", authData);
            TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
            session.sendMessage(textMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/login.html";
    }

    @ExceptionHandler(CobbleServiceException.class)
    public String handleCobbleServiceException(CobbleServiceException e) {
        return "redirect:/error.html";
    }
}
