package io.hhplus.concert.reservation.domain.model;

import java.time.LocalDateTime;

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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}