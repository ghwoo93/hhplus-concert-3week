package io.hhplus.concert.reservation.domain.service;

import java.util.UUID;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.exception.SeatAlreadyReservedException;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.infrastructure.mapper.ReservationMapper;
import io.hhplus.concert.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, SeatRepository seatRepository) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional
    public Reservation reserveSeat(String concertId, int seatNumber, String userId) {
        long startTime = System.currentTimeMillis();

        reserveSeat(concertId, seatNumber, userId);
        Reservation reservation = createReservation(concertId, seatNumber, userId);
        Reservation savedReservation = saveReservation(reservation);

        long endTime = System.currentTimeMillis();
        log.info("Seat reservation took {} ms", endTime - startTime);

        return savedReservation;
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

    private void reserveSeat(String concertId, int seatNumber, String userId) {
        seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)
            .ifPresent(seat -> {
                if (seat.isReserved()) {
                    throw new SeatAlreadyReservedException();
                }
                seat.reserve(userId);
                seatRepository.save(seat);
            });
    }

    private Reservation createReservation(String concertId, int seatNumber, String userId) {
        return new Reservation(
            UUID.randomUUID().toString(),
            userId,
            concertId,
            seatNumber,
            "TEMPORARY",
            LocalDateTime.now()
        );
    }

    private Reservation saveReservation(Reservation reservation) {
        return ReservationMapper.toDomain(
            reservationRepository.save(ReservationMapper.toEntity(reservation))
        );
    }
}
