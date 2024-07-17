package io.hhplus.concert.reservation.presentation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.service.interfaces.ConcertFacade;
import io.hhplus.concert.reservation.infrastructure.mapper.ResponseMapper;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    
    private final ConcertFacade concertFacade;

    @Autowired
    public ReservationController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserveSeat(@RequestBody SeatReservationRequest seatReservationRequest) {
        ReservationResponse reservationResponse = concertFacade.reserveSeat(seatReservationRequest);
        return ResponseEntity.ok(reservationResponse);
    }

    @GetMapping("/dates")
    public ResponseEntity<List<ConcertDateResponse>> getAvailableConcertDates() {
        List<ConcertDTO> concertDates = concertFacade.getAllConcerts();
        List<ConcertDateResponse> response = ResponseMapper.toConcertDateResponseList(concertDates);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertId}/seats")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable String concertId) {
        List<SeatDTO> seats = concertFacade.getSeatsByConcertId(concertId);
        List<SeatResponse> response = ResponseMapper.toSeatResponseList(seats);
        return ResponseEntity.ok(response);
    }
}
