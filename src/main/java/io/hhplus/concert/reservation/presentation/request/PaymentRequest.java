package io.hhplus.concert.reservation.presentation.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequest {
    private String reservationId;
    private BigDecimal amount;
    private String token;
}