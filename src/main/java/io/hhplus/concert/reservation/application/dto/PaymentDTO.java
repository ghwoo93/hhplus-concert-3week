package io.hhplus.concert.reservation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentDTO {
    private String paymentId;
    private String status;
}
