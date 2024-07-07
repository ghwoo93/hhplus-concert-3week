package io.hhplus.concert.reservation.presentation.request;

import lombok.Data;

@Data
public class BalanceRequest {
    private String userId;
    private int amount;
}
