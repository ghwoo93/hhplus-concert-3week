package io.hhplus.concert.reservation.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {
    private String token;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String status;

    public Queue toQueue() {
        Queue queue = new Queue();
        queue.setUserId(this.userId);
        queue.setCreatedAt(this.createdAt);
        queue.setStatus(this.status);
        queue.setLastUpdatedAt(LocalDateTime.now());
        // 큐 위치는 getQueueStatus 메서드에서 계산
        return queue;
    }
}
