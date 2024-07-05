package io.hhplus.concert.reservation.aggregate.dto;

import lombok.Data;

@Data
public class BalanceResponse {
    private int newBalance;
    private int currentBalance;
}
