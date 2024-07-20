package io.hhplus.concert.reservation.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.SeatAlreadyReservedException;
import io.hhplus.concert.reservation.domain.enums.SeatStatus;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;
import io.hhplus.concert.reservation.infrastructure.mapper.ReservationMapper;
import io.hhplus.concert.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;

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
    public Reservation reserveSeat(String concertId, int seatNumber, String userId) {
        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(5);
        int updatedRows = seatRepository.updateSeatStatus(
            concertId,
            seatNumber,
            SeatStatus.AVAILABLE,
            SeatStatus.RESERVED,
            userId,
            reservedUntil
        );

        if (updatedRows == 0) {
            throw new SeatAlreadyReservedException();
        }

        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(
            reservationId,
            userId,
            concertId,
            seatNumber,
            "TEMPORARY",
            LocalDateTime.now()
        );

        return ReservationMapper.toDomain(reservationRepository.save(ReservationMapper.toEntity(reservation)));
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
