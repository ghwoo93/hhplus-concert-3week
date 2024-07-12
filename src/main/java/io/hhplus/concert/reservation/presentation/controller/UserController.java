package io.hhplus.concert.reservation.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.service.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.request.BalanceRequest;
import io.hhplus.concert.reservation.presentation.request.UserRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final ConcertFacade concertFacade;

    @Autowired
    public UserController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> generateToken(@RequestBody UserRequest userRequest) {
        TokenDTO tokenDto = concertFacade.generateToken(userRequest.getUserId());
        return ResponseEntity.ok(tokenDto);
    }

    @PutMapping("/{userId}/points")
    public ResponseEntity<BalanceResponse> rechargeBalance(@PathVariable String userId, @RequestBody BalanceRequest balanceRequest) {
        BigDecimal amount = BigDecimal.valueOf(balanceRequest.getAmount());
        BalanceResponse balanceResponse = concertFacade.rechargeBalance(userId, amount);
        return ResponseEntity.ok(balanceResponse);
    }

    @GetMapping("/{userId}/points")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String userId) {
        BalanceResponse balanceResponse = concertFacade.getBalance(userId);
        return ResponseEntity.ok(balanceResponse);
    }
}


