package io.hhplus.concert.reservation.application.exception;

public class UserNotInQueueException extends RuntimeException {
    public UserNotInQueueException() {
        super("User not found in queue.");
    }

    public UserNotInQueueException(String message) {
        super(message);
    }
}
