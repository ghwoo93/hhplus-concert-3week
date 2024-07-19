package io.hhplus.concert.reservation.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.exception.UserAlreadyInQueueException;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.request.UserTokenRequest;
import io.hhplus.concert.reservation.presentation.response.TokenResponse;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final ConcertFacade concertFacade;

    public UserController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping("/tokens")
    public ResponseEntity<TokenResponse> issueToken(@RequestBody UserTokenRequest request) {
        try {
            TokenDTO tokenDTO = concertFacade.issueToken(request.getUserId());
            TokenResponse response = new TokenResponse(tokenDTO.getToken(), tokenDTO.getQueuePosition(), tokenDTO.getRemainingTime());
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UserAlreadyInQueueException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}


