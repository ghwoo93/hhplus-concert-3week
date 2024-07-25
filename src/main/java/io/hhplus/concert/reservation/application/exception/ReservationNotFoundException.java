package io.hhplus.concert.reservation.application.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException() {
        super("Reservation not found.");
    }
    
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
