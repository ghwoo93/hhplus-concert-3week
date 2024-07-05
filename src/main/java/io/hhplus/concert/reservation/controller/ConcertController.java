package io.hhplus.concert.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.aggregate.dto.BalanceRequest;
import io.hhplus.concert.reservation.aggregate.dto.BalanceResponse;
import io.hhplus.concert.reservation.aggregate.dto.PaymentRequest;
import io.hhplus.concert.reservation.aggregate.dto.PaymentResponse;
import io.hhplus.concert.reservation.aggregate.dto.QueueRequest;
import io.hhplus.concert.reservation.aggregate.dto.QueueResponse;
import io.hhplus.concert.reservation.aggregate.dto.ReservationDateRequest;
import io.hhplus.concert.reservation.aggregate.dto.ReservationSeat;
import io.hhplus.concert.reservation.aggregate.dto.SeatReservationRequest;
import io.hhplus.concert.reservation.aggregate.dto.SeatReservationResponse;
import io.hhplus.concert.reservation.aggregate.dto.UserTokenRequest;
import io.hhplus.concert.reservation.aggregate.dto.UserTokenResponse;
import io.hhplus.concert.reservation.service.facade.ConcertFacade;

import java.util.List;

@RestController
@RequestMapping("/api/concert/v1")
public class ConcertController {

    @Autowired
    private ConcertFacade concertFacade;

    @PostMapping("/token")
    public UserTokenResponse generateUserToken(@RequestBody UserTokenRequest request) {
        return concertFacade.generateUserToken(request);
    }

    @GetMapping("/reservations/dates")
    public List<String> getAvailableDates() {
        return concertFacade.getAvailableDates();
    }

    @GetMapping("/reservations/seats")
    public List<ReservationSeat> getAvailableSeats(@RequestBody ReservationDateRequest request) {
        return concertFacade.getAvailableSeats(request);
    }

    @PostMapping("/reservations")
    public SeatReservationResponse reserveSeat(@RequestBody SeatReservationRequest request) {
        return concertFacade.reserveSeat(request);
    }

    @PostMapping("/balance")
    public BalanceResponse chargeBalance(@RequestBody BalanceRequest request) {
        return concertFacade.chargeBalance(request);
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam String userId) {
        return concertFacade.getBalance(userId);
    }

    @PostMapping("/payments")
    public PaymentResponse makePayment(@RequestBody PaymentRequest request) {
        return concertFacade.makePayment(request);
    }

    @GetMapping("/queue")
    public QueueResponse checkQueue(@RequestParam String token) {
        return concertFacade.checkQueue(new QueueRequest(token));
    }
}
