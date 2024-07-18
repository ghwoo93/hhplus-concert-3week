package io.hhplus.concert.reservation.application.exception;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException() {
        super("Seat not found.");
    }
}