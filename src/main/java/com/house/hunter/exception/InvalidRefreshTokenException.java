package com.house.hunter.exception;

public final class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
