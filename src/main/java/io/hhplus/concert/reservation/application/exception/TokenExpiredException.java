package io.hhplus.concert.reservation.application.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token expired.");
    }
}
