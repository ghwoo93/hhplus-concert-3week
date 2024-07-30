package io.hhplus.concert.reservation.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private final RedissonClient redissonClient;
    
    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, RedissonClient redissonClient) {
        this.paymentRepository = paymentRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(String userId, String reservationId, BigDecimal amount) {
        String lockKey = "lock:payment:" + reservationId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            boolean isLocked = lock.tryLock(10, 5, java.util.concurrent.TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("Failed to acquire lock for payment processing");
            }
            
            Payment payment = createPayment(userId, reservationId, amount);
            Payment savedPayment = savePayment(payment);
            return PaymentMapper.domainToDto(savedPayment);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while trying to acquire lock for payment", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private Payment createPayment(String userId, String reservationId, BigDecimal amount) {
        return new Payment(null, userId, reservationId, amount, "COMPLETED", LocalDateTime.now());
    }

    private Payment savePayment(Payment payment) {
        return PaymentMapper.entityToDomain(paymentRepository.save(PaymentMapper.domainToEntity(payment)));
    }
}