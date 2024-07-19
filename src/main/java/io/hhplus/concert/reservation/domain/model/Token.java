package io.hhplus.concert.reservation.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String token;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String status;

    public Token(String token, String userId, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    private Token(String userId) {
        this.token = UUID.randomUUID().toString();
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24); // 24시간 후 만료
        this.status = "ACTIVE";
    }

    public Queue toQueue() {
        Queue queue = new Queue();
        queue.setUserId(this.userId);
        queue.setCreatedAt(this.createdAt);
    
        // status 변환 로직에 예외 처리 추가
        try {
            queue.setStatus(Queue.QueueStatus.valueOf(this.status.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            // 기본값 설정 또는 로그 기록 등의 예외 처리
            queue.setStatus(Queue.QueueStatus.WAITING); // 예: 기본값으로 WAITING 설정
            // logger.warn("Invalid status value: " + this.status + ". Setting to WAITING.");
        }
    
        queue.setLastUpdatedAt(LocalDateTime.now());
        // 큐 위치는 getQueueStatus 메서드에서 계산
        return queue;
    }

    public static Token createNewToken(String userId) {
        return new Token(userId);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void invalidate() {
        this.expiresAt = LocalDateTime.now();
        this.status = "EXPIRED";
    }
}