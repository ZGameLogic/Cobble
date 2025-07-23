package com.zgamelogic.controllers;

import com.zgamelogic.data.CobbleServiceException;
import com.zgamelogic.data.player.Player;
import com.zgamelogic.data.websocket.PlayerDTO;
import com.zgamelogic.services.CobbleService;
import com.zgamelogic.websocket.annotations.*;
import com.zgamelogic.websocket.data.WebSocketMessage;
import lombok.AllArgsConstructor;

import static com.zgamelogic.data.Constants.*;

@AllArgsConstructor
@WebSocketController
public class CobbleWebSocket {
    private final CobbleService cobbleService;

    @WebSocketMapping(type = CONTINUE)
    private PlayerDTO initialData(@WebSocketAttribute(DISCORD_ASPECT_ID) long userId) throws CobbleServiceException {
        Player player = cobbleService.getCobblePlayer(userId);
        return new PlayerDTO(player);
    }

    @WebSocketMapping(type = START_ID)
    private PlayerDTO startCobbleGame(@WebSocketData String townName, @WebSocketAttribute(DISCORD_ASPECT_ID) long userId) throws CobbleServiceException {
        Player player = cobbleService.startCobblePlayer(userId, townName);
        return new PlayerDTO(player);
    }

    @WebSocketMapping(type = GAME_CHECK)
    private boolean checkForGame(@WebSocketAttribute(DISCORD_ASPECT_ID) long userId){
        return cobbleService.isPlayerStarted(userId);
    }

    @WebSocketExceptionHandler(CobbleServiceException.class)
    private WebSocketMessage handleCobbleServiceException(CobbleServiceException e, WebSocketMessage message) {
        return new WebSocketMessage(
            message.getType(),
            message.getSubtype(),
            message.getReplyId(),
            400,
            e.getMessage()
        );
    }
}
