package io.hhplus.concert.reservation.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "reservation_id")
    private String reservationId;
    
    private BigDecimal amount;
    
    @Column(name = "payment_status")
    private String paymentStatus;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
