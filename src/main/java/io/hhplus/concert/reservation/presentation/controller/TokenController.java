package io.hhplus.concert.reservation.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.request.TokenRequest;
import io.hhplus.concert.reservation.presentation.response.TokenResponse;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {
    
    private final ConcertFacade concertFacade;
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);


    public TokenController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenRequest request) {
        TokenDTO tokenDTO = concertFacade.issueToken(request.getUserId());
        TokenResponse response = new TokenResponse(
            tokenDTO.getToken(),
            tokenDTO.getStatus(),
            tokenDTO.getQueuePosition(),
            tokenDTO.getRemainingTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<TokenResponse> checkTokenStatus(@PathVariable String userId) {
        TokenDTO tokenDTO = concertFacade.checkTokenStatus(userId);
        TokenResponse response = new TokenResponse(
            tokenDTO.getToken(),
            tokenDTO.getStatus(),
            tokenDTO.getQueuePosition(),
            tokenDTO.getRemainingTime()
        );
        return ResponseEntity.ok(response);
    }
}

