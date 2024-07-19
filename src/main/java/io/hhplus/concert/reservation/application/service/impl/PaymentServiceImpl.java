package io.hhplus.concert.reservation.application.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.exception.InsufficientBalanceException;
import io.hhplus.concert.reservation.application.exception.ReservationNotFoundException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.PaymentService;
import io.hhplus.concert.reservation.domain.model.Payment;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.domain.model.User;
import io.hhplus.concert.reservation.infrastructure.entity.PaymentEntity;
import io.hhplus.concert.reservation.infrastructure.entity.ReservationEntity;
import io.hhplus.concert.reservation.infrastructure.repository.PaymentRepository;
import io.hhplus.concert.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, ReservationRepository reservationRepository, TokenRepository tokenRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public PaymentDTO processPayment(String reservationId, BigDecimal amount, String token) {
        ReservationEntity reservationEntity = reservationRepository.findById(reservationId).orElseThrow(ReservationNotFoundException::new);
        TokenEntity tokenEntity = tokenRepository.findByToken(token).orElseThrow(TokenNotFoundException::new);
        User user = mapToDomain(tokenEntity.getUser());
        
        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        
        user.setBalance(user.getBalance().subtract(amount));
        Payment payment = new Payment(UUID.randomUUID().toString(), user.getId(), reservationEntity.getId(), amount, "COMPLETED", LocalDateTime.now());
        PaymentEntity paymentEntity = mapToEntity(payment);
        paymentRepository.save(paymentEntity);
        reservationEntity.setReservationStatus("COMPLETED");
        reservationRepository.save(reservationEntity);
        
        return new PaymentDTO(payment.getId(), "success");
    }

    private User mapToDomain(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), userEntity.getBalance());
    }

    private PaymentEntity mapToEntity(Payment payment) {
        return new PaymentEntity(payment.getId(), payment.getUserId(), payment.getReservationId(), payment.getAmount(), payment.getPaymentStatus(), payment.getPaidAt());
    }
}