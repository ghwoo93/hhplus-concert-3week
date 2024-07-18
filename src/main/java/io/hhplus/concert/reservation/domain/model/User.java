package io.hhplus.concert.reservation.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String username;
    private String password;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public void rechargeBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Recharge amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    public void deductBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deduct amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }    
}
