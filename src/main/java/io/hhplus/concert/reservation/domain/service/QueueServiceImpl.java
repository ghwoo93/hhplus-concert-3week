package io.hhplus.concert.reservation.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
        Queue queue = queueRepository.findByToken(token)
                .map(QueueMapper::toModel)
                .orElseThrow(() -> new TokenNotFoundException());

        if (queue.isExpired()) {
            queue.setStatus(Queue.QueueStatus.EXPIRED);
            queueRepository.save(QueueMapper.toEntity(queue));
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
        List<Queue> waitingQueues = queueRepository.findByStatusOrderByCreatedAt(Queue.QueueStatus.WAITING.name())
                .stream()
                .map(QueueMapper::toModel)
                .collect(Collectors.toList());
        int position = calculatePosition(waitingQueues, queue.getUserId());
        queue.setQueuePosition(position);
        queueRepository.save(QueueMapper.toEntity(queue));
    }

    private int calculatePosition(List<Queue> waitingQueues, String userId) {
        for (int i = 0; i < waitingQueues.size(); i++) {
            if (waitingQueues.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        throw new UserNotInQueueException();
    }

    @Override
    @Transactional
    public Queue createNewQueue(String userId) {
        Queue newQueue = new Queue(userId);
        newQueue.setQueuePosition(calculateQueuePosition());
        return QueueMapper.toModel(queueRepository.save(QueueMapper.toEntity(newQueue)));
    }

    private int calculateQueuePosition() {
        return (int) queueRepository.count() + 1;
    }

    @Override
    @Transactional
    public Queue getOrCreateQueueForUser(String userId) {
        return queueRepository.findByUserId(userId)
                .map(QueueMapper::toModel)
                .orElseGet(() -> createNewQueue(userId));
    }
}
