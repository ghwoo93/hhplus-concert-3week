package io.hhplus.concert.reservation.presentation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.service.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.request.BalanceRequest;
import io.hhplus.concert.reservation.presentation.request.PaymentRequest;
import io.hhplus.concert.reservation.presentation.request.QueueRequest;
import io.hhplus.concert.reservation.presentation.request.ReservationRequest;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.request.UserRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.PaymentResponse;
import io.hhplus.concert.reservation.presentation.response.QueueStatusResponse;
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
    public ResponseEntity<ReservationResponse> reserveSeat(@RequestBody ReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = concertFacade.reserveSeat(reservationRequest.getConcertId(), reservationRequest.getSeatNumber(), reservationRequest.getToken());
        return ResponseEntity.ok(reservationResponse);
    }

    @GetMapping("/dates")
    public ResponseEntity<List<ConcertDateResponse>> getAvailableConcertDates() {
        List<ConcertDateResponse> concertDates = concertFacade.getAvailableConcertDates();
        return ResponseEntity.ok(concertDates);
    }

    @GetMapping("/{concertId}/seats")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable String concertId) {
        List<SeatResponse> seats = concertFacade.getAvailableSeats(concertId);
        return ResponseEntity.ok(seats);
    }
}
