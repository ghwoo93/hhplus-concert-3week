package io.hhplus.concert.reservation.aggregate.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentId;
    private String status;
}
