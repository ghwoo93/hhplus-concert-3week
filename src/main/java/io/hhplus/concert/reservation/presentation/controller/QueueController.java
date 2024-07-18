package io.hhplus.concert.reservation.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.exception.TokenExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.exception.UserAlreadyInQueueException;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.request.QueueRequest;
import io.hhplus.concert.reservation.presentation.request.QueueStatusRequest;
import io.hhplus.concert.reservation.presentation.response.QueueResponse;
import io.hhplus.concert.reservation.presentation.response.QueueStatusResponse;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
    
    private final ConcertFacade concertFacade;
    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);


    public QueueController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @PostMapping("/in")
    public ResponseEntity<QueueResponse> createQueue(@RequestBody QueueRequest request) {
        logger.info("Received create queue request for user: {}", request.getUserId());

        try {
            QueueDTO queueDTO = concertFacade.createQueue(request.getUserId());
            logger.info("Queue created: {}", queueDTO);
            QueueResponse response = new QueueResponse(queueDTO.getQueuePosition(), queueDTO.getStatus(), queueDTO.getRemainingTime());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UserAlreadyInQueueException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> checkQueueStatus(@RequestBody QueueStatusRequest request) {
        try {
            QueueDTO queueDTO = concertFacade.checkQueueStatus(request.getToken());
            QueueStatusResponse response = new QueueStatusResponse(queueDTO.getQueuePosition(), queueDTO.getRemainingTime());
            return ResponseEntity.ok(response);
        } catch (TokenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

