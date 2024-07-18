package io.hhplus.concert.reservation.domain.service;

import io.hhplus.concert.reservation.domain.model.Queue;

public interface QueueService {
    Queue getQueueStatus(String token);
    void updateQueuePosition(Queue queue);
    Queue createNewQueue(String userId);
    Queue getOrCreateQueueForUser(String userId);
}
