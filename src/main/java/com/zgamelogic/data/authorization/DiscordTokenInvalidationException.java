package com.zgamelogic.data.authorization;

public class DiscordTokenInvalidationException extends RuntimeException {
    public DiscordTokenInvalidationException(String message) {
        super(message);
    }
}
