package io.hhplus.concert.reservation.application.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient balance.");
    }
}
