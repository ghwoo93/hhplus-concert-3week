package io.hhplus.concert.reservation.application.exception;

public class ConcertNotFoundException extends RuntimeException {
    public ConcertNotFoundException() {
        super("Concert not found.");
    }
}
