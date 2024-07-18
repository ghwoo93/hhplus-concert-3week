package io.hhplus.concert.reservation.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;

import io.hhplus.concert.reservation.presentation.request.BalanceRequest;
import io.hhplus.concert.reservation.presentation.request.PaymentRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.PaymentResponse;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    private final ConcertFacade concertFacade;

    @Autowired
    public PaymentController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping("/recharge")
    public ResponseEntity<BalanceResponse> rechargeBalance(@RequestBody BalanceRequest request) {
        BalanceResponse response = concertFacade.rechargeBalance(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String userId) {
        BalanceResponse response = concertFacade.getBalance(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentDTO paymentDTO = concertFacade.processPayment(request.getReservationId(), request.getAmount(), request.getToken());
        PaymentResponse response = new PaymentResponse(paymentDTO.getPaymentId(), paymentDTO.getStatus());
        return ResponseEntity.ok(response);
    }
}
