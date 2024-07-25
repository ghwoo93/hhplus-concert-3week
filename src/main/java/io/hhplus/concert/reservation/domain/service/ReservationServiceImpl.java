package io.hhplus.concert.reservation.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.SeatAlreadyReservedException;
import io.hhplus.concert.reservation.domain.enums.SeatStatus;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.infrastructure.entity.ReservationEntity;
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
    public Reservation reserveSeat(String concertId, int seatNumber, String userId, LocalDate performanceDate) {
        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(5);

        logger.debug("[reserveSeat]"+ userId + " is trying to reserve seat " + seatNumber + " for concert " + concertId);

        int updatedRows = seatRepository.updateSeatStatus(
            concertId,
            seatNumber,
            SeatStatus.AVAILABLE,
            SeatStatus.RESERVED,
            userId,
            reservedUntil
        );

        logger.debug("[reserveSeat]"+ updatedRows + " rows updated for seat " + seatNumber + " for concert " + concertId);

        if (updatedRows == 0) {
            throw new SeatAlreadyReservedException();
        }

        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = createReservation(reservationId, concertId, seatNumber, userId, performanceDate);
        logger.debug("[reserveSeat]"+ reservationId + " is created for " + userId + " for concert " + concertId);
        
        Reservation savedEntity = saveReservation(reservation);
        logger.debug("[reserveSeat]saved entity:"+savedEntity.toString());

        return savedEntity;
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
