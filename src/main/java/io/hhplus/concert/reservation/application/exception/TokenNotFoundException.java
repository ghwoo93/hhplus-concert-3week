package io.hhplus.concert.reservation.application.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super("Token not found.");
    }
}
