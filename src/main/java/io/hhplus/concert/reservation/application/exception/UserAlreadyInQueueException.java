package io.hhplus.concert.reservation.application.exception;

public class UserAlreadyInQueueException extends RuntimeException {
    public UserAlreadyInQueueException(String message) {
        super(message);
    }
}