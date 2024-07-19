package io.hhplus.concert.reservation.domain.model;

import java.time.LocalDateTime;

import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import lombok.Getter;

import lombok.Setter;


@Getter
@Setter
public class Token {
    private String id;
    private String userId;
    private int queuePosition;
    private TokenStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUpdatedAt;

    public Token(String userId) {
        this.userId = userId;
        this.status = TokenStatus.WAITING;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(1);
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void updateQueuePosition(int newPosition) {
        this.queuePosition = newPosition;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public long getRemainingTimeInSeconds() {
        LocalDateTime now = LocalDateTime.now();
        return Math.max(0, java.time.Duration.between(now, this.expiresAt).getSeconds());
    }

    public void expire() {
        this.status = TokenStatus.EXPIRED;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}