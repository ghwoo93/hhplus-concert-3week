package io.hhplus.concert.reservation.application.service.interfaces;

import io.hhplus.concert.reservation.application.dto.QueueDTO;

public interface QueueService {
    QueueDTO getQueueStatus(String token);
}
