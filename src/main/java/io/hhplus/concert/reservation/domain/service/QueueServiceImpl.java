package io.hhplus.concert.reservation.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.QueueExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.exception.UserNotInQueueException;
import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.infrastructure.mapper.QueueMapper;
import io.hhplus.concert.reservation.infrastructure.repository.QueueRepository;

@Service
public class QueueServiceImpl implements QueueService {
    
    
    private final QueueRepository queueRepository;

    @Autowired
    public QueueServiceImpl(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Queue getQueueStatus(String token) {
        Queue queue = findQueueByToken(token);
        if (queue.isExpired()) {
            expireQueue(queue);
            throw new QueueExpiredException();
        }
        if (queue.getStatus() == Queue.QueueStatus.WAITING) {
            updateQueuePosition(queue);
        }
        return queue;
    }

    @Override
    @Transactional
    public void updateQueuePosition(Queue queue) {
        List<Queue> waitingQueues = getWaitingQueues();
        int position = calculatePosition(waitingQueues, queue.getUserId());
        queue.updateQueuePosition(position);
        saveQueue(queue);
    }

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    @Transactional
    public void updateQueuePositions() {
        if (updateLock.tryLock()) {
            try {
                List<Queue> waitingQueues = queueRepository.findByStatusOrderByCreatedAt(Queue.QueueStatus.WAITING);
                for (int i = 0; i < waitingQueues.size(); i++) {
                    Queue queue = waitingQueues.get(i);
                    queue.updateQueuePosition(i + 1);
                    queueRepository.save(queue);
                }
            } finally {
                updateLock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public Queue createNewQueue(String userId) {
        Queue newQueue = new Queue(userId);
        newQueue.updateQueuePosition(calculateQueuePosition());
        return saveQueue(newQueue);
    }

    @Override
    @Transactional
    public Queue getOrCreateQueueForUser(String userId) {
        return queueRepository.findByUserId(userId)
                .map(QueueMapper::toModel)
                .orElseGet(() -> createNewQueue(userId));
    }

    private Queue findQueueByToken(String token) {
        return queueRepository.findByToken(token)
                .map(QueueMapper::toModel)
                .orElseThrow(() -> new TokenNotFoundException());
    }

    private void expireQueue(Queue queue) {
        queue.expire();
        saveQueue(queue);
    }

    private List<Queue> getWaitingQueues() {
        return queueRepository.findByStatusOrderByCreatedAt(Queue.QueueStatus.WAITING.name())
                .stream()
                .map(QueueMapper::toModel)
                .collect(Collectors.toList());
    }

    private int calculatePosition(List<Queue> waitingQueues, String userId) {
        for (int i = 0; i < waitingQueues.size(); i++) {
            if (waitingQueues.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        throw new UserNotInQueueException();
    }

    private int calculateQueuePosition() {
        return (int) queueRepository.count() + 1;
    }

    private Queue saveQueue(Queue queue) {
        return QueueMapper.toModel(queueRepository.save(QueueMapper.toEntity(queue)));
    }
}
