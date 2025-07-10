package com.zgamelogic.data.authorization;

public record DiscordToken(
    String token_type,
    String access_token,
    Long expires_in,
    String refresh_token,
    String scope
) {}
