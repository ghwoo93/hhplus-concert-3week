package io.hhplus.concert.reservation.application.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.exception.InsufficientBalanceException;
import io.hhplus.concert.reservation.application.exception.ReservationNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.PaymentService;
import io.hhplus.concert.reservation.domain.model.Payment;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.domain.model.User;
import io.hhplus.concert.reservation.infrastructure.entity.ReservationEntity;
import io.hhplus.concert.reservation.infrastructure.entity.UserEntity;
import io.hhplus.concert.reservation.infrastructure.mapper.PaymentMapper;
import io.hhplus.concert.reservation.infrastructure.mapper.ReservationMapper;
import io.hhplus.concert.reservation.infrastructure.repository.PaymentRepository;
import io.hhplus.concert.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert.reservation.infrastructure.repository.UserRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, ReservationRepository reservationRepository,
                              UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(String reservationId, BigDecimal amount, String token) {
        ReservationEntity reservationEntity = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        Reservation reservation = ReservationMapper.toDomain(reservationEntity);
        
        UserEntity userEntity = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user = userEntity.toUser();
        
        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(new UserEntity(user));
        
        Payment payment = new Payment(user.getId(), reservationId, amount, "COMPLETED", LocalDateTime.now());
        Payment savedPayment = PaymentMapper.entityToDomain(paymentRepository.save(PaymentMapper.domainToEntity(payment)));
        
        reservationEntity.setReservationStatus("COMPLETED");
        reservationRepository.save(reservationEntity);
        
        return PaymentMapper.domainToDto(savedPayment);
    }
}