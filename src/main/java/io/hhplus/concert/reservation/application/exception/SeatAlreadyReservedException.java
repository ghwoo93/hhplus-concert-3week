package io.hhplus.concert.reservation.application.exception;

public class SeatAlreadyReservedException extends RuntimeException {
    public SeatAlreadyReservedException() {
        super("Seat is already reserved.");
    }
}
