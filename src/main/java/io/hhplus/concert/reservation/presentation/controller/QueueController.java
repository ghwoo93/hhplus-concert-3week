package io.hhplus.concert.reservation.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.service.facade.ConcertFacadeImpl;
import io.hhplus.concert.reservation.presentation.request.QueueRequest;
import io.hhplus.concert.reservation.presentation.response.QueueStatusResponse;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
    
    private final ConcertFacadeImpl concertFacade;

    @Autowired
    public QueueController(ConcertFacadeImpl concertFacade) {
        this.concertFacade = concertFacade;
    }

    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(@RequestBody QueueRequest queueRequest) {
        QueueStatusResponse queueStatusResponse = concertFacade.getQueueStatus(queueRequest.getToken());
        return ResponseEntity.ok(queueStatusResponse);
    }
}

