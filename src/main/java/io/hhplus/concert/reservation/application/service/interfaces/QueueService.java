package io.hhplus.concert.reservation.application.service.interfaces;

import io.hhplus.concert.reservation.domain.model.Queue;

public interface QueueService {
    Queue getQueueStatus(String token);
    void updateQueuePosition(Queue queue);
    Queue createNewQueue(String userId);
}
