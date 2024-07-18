package io.hhplus.concert.reservation.domain.service;

import java.math.BigDecimal;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;

public interface PaymentService {
    PaymentDTO processPayment(String userId, String reservationId, BigDecimal amount);
}
