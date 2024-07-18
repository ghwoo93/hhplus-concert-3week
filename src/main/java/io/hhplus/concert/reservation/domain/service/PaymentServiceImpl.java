package io.hhplus.concert.reservation.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.domain.model.Payment;
import io.hhplus.concert.reservation.infrastructure.mapper.PaymentMapper;
import io.hhplus.concert.reservation.infrastructure.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    
    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(String userId, String reservationId, BigDecimal amount) {
        Payment payment = new Payment(null, userId, reservationId, amount, "COMPLETED", LocalDateTime.now());
        Payment savedPayment = PaymentMapper.entityToDomain(paymentRepository.save(PaymentMapper.domainToEntity(payment)));
        return PaymentMapper.domainToDto(savedPayment);
    }
}