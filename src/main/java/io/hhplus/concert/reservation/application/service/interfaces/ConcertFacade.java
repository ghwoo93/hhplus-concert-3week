package io.hhplus.concert.reservation.application.service.interfaces;

import java.util.List;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;

public interface ConcertFacade {
    TokenDTO issueToken(String userId);
    QueueDTO checkQueueStatus(String token);
    QueueDTO createQueue(String userId);
    List<ConcertDTO> getAllConcerts();
    List<SeatDTO> getSeatsByConcertId(String concertId);
}