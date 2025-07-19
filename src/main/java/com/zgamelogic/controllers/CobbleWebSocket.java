package com.zgamelogic.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zgamelogic.data.CobbleServiceException;
import com.zgamelogic.data.View;
import com.zgamelogic.data.player.Player;
import com.zgamelogic.services.CobbleService;
import com.zgamelogic.websocket.annotations.WebSocketAttribute;
import com.zgamelogic.websocket.annotations.WebSocketController;
import com.zgamelogic.websocket.annotations.WebSocketMapping;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@WebSocketController
public class CobbleWebSocket {
    private final CobbleService cobbleService;

    @JsonView(View.InitialData.class)
    @WebSocketMapping(type = "INITIAL")
    public Player initialData(@WebSocketAttribute("Discord-ID") long userId) throws CobbleServiceException {
        return cobbleService.getCobblePlayer(userId);
    }
}
