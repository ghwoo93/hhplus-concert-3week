package io.hhplus.concert.reservation.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.service.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.request.PaymentRequest;
import io.hhplus.concert.reservation.presentation.response.PaymentResponse;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    private final ConcertFacade concertFacade;

    @Autowired
    public PaymentController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = concertFacade.processPayment(paymentRequest.getReservationId(), paymentRequest.getAmount(), paymentRequest.getToken());
        return ResponseEntity.ok(paymentResponse);
    }
}
