package io.hhplus.concert.reservation.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.hhplus.concert.reservation.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private String id;
    private String username;
    private String password;
    private BigDecimal balance;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserEntity(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.balance = user.getBalance();
        this.createdAt = user.getCreatedAt();
    }

    public User toUser() {
        return new User(id, username, password, balance, createdAt);
    }
}
