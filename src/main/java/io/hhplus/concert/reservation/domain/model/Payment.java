package io.hhplus.concert.reservation.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Payment {
    private String id;
    private String userId;
    private String reservationId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paidAt;
}
