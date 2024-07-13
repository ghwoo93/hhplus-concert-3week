package io.hhplus.concert.reservation.infrastructure.entity;

import java.time.LocalDateTime;

import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.domain.model.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenEntity {
    @Id
    private String token;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public TokenEntity(Token token) {
        this.token = token.getToken();
        this.userId = token.getUserId();
        this.createdAt = token.getCreatedAt();
        this.expiresAt = token.getExpiresAt();
    }

    public Token toToken() {
        return new Token(token, userId, createdAt, expiresAt);
    }

    // toQueue 메서드 제거 또는 수정
    public Queue toQueue() {
        Queue queue = new Queue();
        queue.setUserId(this.userId);
        queue.setToken(this.token);
        queue.setCreatedAt(this.createdAt);
        queue.setExpiresAt(this.expiresAt);
        return queue;
    }
}
