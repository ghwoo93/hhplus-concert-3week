package io.hhplus.concert.reservation.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Queue {
    private Long id;
    private String userId;
    private String token;
    private int queuePosition;
    private QueueStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUpdatedAt;

    public Queue(String userId) {
        this.userId = userId;
        this.status = QueueStatus.WAITING;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(1);
        this.lastUpdatedAt = LocalDateTime.now();
        this.token = generateToken();
    }

    public long getRemainingTimeInSeconds() {
        LocalDateTime now = LocalDateTime.now();
        
        if (lastUpdatedAt == null) {
            lastUpdatedAt = now;
        }
        
        switch (status) {
            case WAITING:
                LocalDateTime nextUpdate = this.lastUpdatedAt.plusSeconds(10);
                return Math.max(0, ChronoUnit.SECONDS.between(now, nextUpdate));
            case ACTIVE:
                return Math.max(0, ChronoUnit.SECONDS.between(now, expiresAt));
            case EXPIRED:
            default:
                return 0;
        }
    }

    public void setExpirationTime(long secondsFromNow) {
        this.expiresAt = LocalDateTime.now().plusSeconds(secondsFromNow);
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public long getRemainingTime() {
        return Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    private String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public enum QueueStatus {
        ACTIVE, WAITING, EXPIRED;

        public static QueueStatus fromString(String status) {
            return valueOf(status.toUpperCase());
        }
    }
}