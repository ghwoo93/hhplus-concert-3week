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
import io.hhplus.concert.reservation.application.exception.TokenExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.request.TokenRequest;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {
    
    private final ConcertFacade concertFacade;
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);


    public TokenController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping
    public ResponseEntity<TokenDTO> issueToken(@RequestBody TokenRequest request) {
        TokenDTO tokenDTO = concertFacade.issueToken(request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<TokenDTO> checkTokenStatus(@PathVariable String userId) {
        try {
            TokenDTO tokenDTO = concertFacade.checkTokenStatus(userId);
            return ResponseEntity.ok(tokenDTO);
        } catch (TokenNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
    }
}

