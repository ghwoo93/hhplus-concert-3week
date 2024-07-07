package io.hhplus.concert.reservation.presentation.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private String reservationId;
    private int amount;
    private String token;
}