package io.hhplus.concert.reservation.presentation.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BalanceRequest {
    private String userId;
    private BigDecimal amount;
}
