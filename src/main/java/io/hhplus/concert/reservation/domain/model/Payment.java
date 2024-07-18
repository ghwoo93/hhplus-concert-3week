package io.hhplus.concert.reservation.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Payment {
    private Long id;
    private String userId;
    private String reservationId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paidAt;

    // 새로운 결제를 생성할 때 사용할 생성자 (ID는 null로 설정)
    public Payment(String userId, String reservationId, BigDecimal amount, String paymentStatus, LocalDateTime paidAt) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
    }    
}
