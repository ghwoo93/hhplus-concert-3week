package io.hhplus.concert.reservation.application.exception;

public class QueueExpiredException extends RuntimeException {
    public QueueExpiredException() {
        super("Queue Expired.");
    }
}
