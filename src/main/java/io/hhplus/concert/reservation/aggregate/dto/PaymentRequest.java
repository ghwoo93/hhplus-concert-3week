package io.hhplus.concert.reservation.aggregate.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String reservationId;
    private int amount;
    private String token;
}