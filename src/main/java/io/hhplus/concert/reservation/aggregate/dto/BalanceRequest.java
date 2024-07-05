package io.hhplus.concert.reservation.aggregate.dto;

import lombok.Data;

@Data
public class BalanceRequest {
    private String userId;
    private int amount;
}
