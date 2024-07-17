package io.hhplus.concert.reservation.application.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.dto.ReservationDTO;
import io.hhplus.concert.reservation.application.exception.SeatAlreadyReservedException;
import io.hhplus.concert.reservation.application.exception.SeatNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.ReservationService;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.domain.model.Seat;
import io.hhplus.concert.reservation.infrastructure.mapper.ReservationMapper;
import io.hhplus.concert.reservation.infrastructure.repository.ConcertRepository;
import io.hhplus.concert.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;
import io.hhplus.concert.reservation.presentation.controller.QueueController;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)
            .ifPresent(seat -> {
                if (seat.isReserved()) {
                    throw new SeatAlreadyReservedException();
                }
                seat.setReserved(true);
                seat.setReservedBy(userId);
                seat.setReservedUntil(LocalDateTime.now().plusMinutes(5));
                seatRepository.save(seat);
            });

        Reservation reservation = new Reservation(
            UUID.randomUUID().toString(),
            userId,
            concertId,
            seatNumber,
            "TEMPORARY",
            LocalDateTime.now()
        );

        Reservation savedReservation = ReservationMapper.toDomain(
            reservationRepository.save(ReservationMapper.toEntity(reservation))
        );

        long endTime = System.currentTimeMillis();
        logger.info("Seat reservation took {} ms", endTime - startTime);

        return savedReservation;
    }

    // @Override
    // public ReservationDTO reserveSeat(String concertId, int seatNumber, String token) {
    //     Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber).orElseThrow(() -> new SeatNotFoundException());
    //     if (seat.isReserved()) {
    //         throw new SeatAlreadyReservedException();
    //     }
    //     seat.setReserved(true);
    //     seat.setReservedBy(token);
    //     seat.setReservedUntil(LocalDateTime.now().plusMinutes(5));
    //     seatRepository.save(seat);
    //     Reservation reservation = new Reservation(UUID.randomUUID().toString(), concertId, seatNumber, "RESERVED", LocalDateTime.now());
    //     reservationRepository.save(reservation);
    //     return new ReservationDTO(reservation.getId(), seat.getReservedUntil());
    // }

    // @Override
    // public List<ConcertDateResponse> getAvailableConcertDates() {
    //     return concertRepository.findAll().stream()
    //         .map(concert -> new ConcertDateResponse(concert.getId(), concert.getConcertName(), concert.getDate()))
    //         .collect(Collectors.toList());
    // }

    // @Override
    // public List<SeatResponse> getAvailableSeats(String concertId) {
    //     return seatRepository.findByConcertId(concertId).stream()
    //         .map(seat -> new SeatResponse(seat.getSeatNumber(), !seat.isReserved()))
    //         .collect(Collectors.toList());
    // }
}
