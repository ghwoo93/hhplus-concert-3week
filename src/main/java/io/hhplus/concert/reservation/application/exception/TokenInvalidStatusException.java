package io.hhplus.concert.reservation.application.exception;

public class TokenInvalidStatusException extends RuntimeException {
    public TokenInvalidStatusException() {
        super("Token has invalid status.");
    }
}
