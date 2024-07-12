package io.hhplus.concert.reservation.application.service.interfaces;

import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;

public interface ConcertFacade {
    TokenDTO issueToken(String userId);
    QueueDTO checkQueueStatus(String token);
}