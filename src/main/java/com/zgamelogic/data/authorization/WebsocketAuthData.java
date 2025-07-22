package com.zgamelogic.data.authorization;

public record WebsocketAuthData(
    String rollingToken,
    Long userId,
    String avatar,
    String username
) {}
