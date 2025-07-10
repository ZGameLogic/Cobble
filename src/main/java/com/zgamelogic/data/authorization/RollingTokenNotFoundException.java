package com.zgamelogic.data.authorization;

public class RollingTokenNotFoundException extends RuntimeException {
    public RollingTokenNotFoundException(String message) {
        super(message);
    }
}
