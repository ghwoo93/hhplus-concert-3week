package io.hhplus.concert.reservation.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.SeatAlreadyReservedException;
import io.hhplus.concert.reservation.domain.enums.SeatStatus;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.infrastructure.mapper.ReservationMapper;
import io.hhplus.concert.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final RedissonClient redissonClient;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, 
                                  SeatRepository seatRepository, 
                                  RedissonClient redissonClient) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    @Transactional
    public Reservation reserveSeat(String concertId, int seatNumber, String userId, LocalDate performanceDate) {
        String lockKey = "lock:concert:" + concertId + ":seat:" + seatNumber;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("Failed to acquire lock for seat reservation");
            }
            
            // 좌석 상태 확인 및 예약 로직
            int updatedRows = seatRepository.updateSeatStatus(
                concertId,
                seatNumber,
                SeatStatus.AVAILABLE,
                SeatStatus.RESERVED,
                userId,
                LocalDateTime.now().plusMinutes(5)
            );

            if (updatedRows == 0) {
                throw new SeatAlreadyReservedException();
            }

            String reservationId = UUID.randomUUID().toString();
            Reservation reservation = createReservation(reservationId, concertId, seatNumber, userId, performanceDate);
            return saveReservation(reservation);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while trying to acquire lock", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation getReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .map(ReservationMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateReservationStatus(String reservationId, String status) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            reservation.setReservationStatus(status);
            reservationRepository.save(reservation);
        });
    }

    private Reservation createReservation(String id, String concertId, int seatNumber, String userId, LocalDate performanceDate) {
        return new Reservation(
            id,
            userId,
            concertId,
            seatNumber,
            "TEMPORARY",
            LocalDateTime.now(),
            performanceDate
        );
    }

    private Reservation saveReservation(Reservation reservation) {
        return ReservationMapper.toDomain(
            reservationRepository.save(ReservationMapper.toEntity(reservation))
        );
    }
}
