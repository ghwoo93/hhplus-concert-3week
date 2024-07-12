package io.hhplus.concert.reservation.presentation.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserResponse {
    private String userId;
    private BigDecimal balance;
}
