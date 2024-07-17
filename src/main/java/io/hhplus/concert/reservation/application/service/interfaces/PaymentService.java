package io.hhplus.concert.reservation.application.service.interfaces;

import java.math.BigDecimal;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;

public interface PaymentService {
    PaymentDTO processPayment(String reservationId, BigDecimal amount, String token);
}
