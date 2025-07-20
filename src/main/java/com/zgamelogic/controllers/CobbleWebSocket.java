package com.zgamelogic.controllers;

import com.zgamelogic.data.CobbleServiceException;
import com.zgamelogic.data.player.Player;
import com.zgamelogic.data.websocket.PlayerDTO;
import com.zgamelogic.services.CobbleService;
import com.zgamelogic.websocket.annotations.WebSocketAttribute;
import com.zgamelogic.websocket.annotations.WebSocketController;
import com.zgamelogic.websocket.annotations.WebSocketMapping;
import lombok.AllArgsConstructor;

import static com.zgamelogic.data.Constants.*;

@AllArgsConstructor
@WebSocketController
public class CobbleWebSocket {
    private final CobbleService cobbleService;

    @WebSocketMapping(type = INITIAL)
    private PlayerDTO initialData(@WebSocketAttribute(DISCORD_ASPECT_ID) long userId) throws CobbleServiceException {
        Player player = cobbleService.getCobblePlayer(userId);
        return new PlayerDTO(player);
    }
}
