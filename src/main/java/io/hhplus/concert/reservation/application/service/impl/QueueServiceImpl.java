package io.hhplus.concert.reservation.application.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.exception.QueueExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.QueueService;
import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.infrastructure.entity.QueueEntity;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;
import io.hhplus.concert.reservation.infrastructure.repository.QueueRepository;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;
import io.hhplus.concert.reservation.presentation.response.QueueStatusResponse;

@Service
public class QueueServiceImpl implements QueueService {
    
    private final TokenRepository tokenRepository;
    private final QueueRepository queueRepository;

    @Autowired
    public QueueServiceImpl(TokenRepository tokenRepository, QueueRepository queueRepository) {
        this.tokenRepository = tokenRepository;
        this.queueRepository = queueRepository;
    }

    @Override
    public QueueStatusResponse getQueueStatus(String token) {
        TokenEntity tokenEntity = tokenRepository.findByToken(token)
            .orElseThrow(() -> new TokenNotFoundException("토큰을 찾을 수 없습니다."));
        
        Queue queue = convertToQueue(tokenEntity);
        
        switch (Queue.QueueStatus.valueOf(queue.getStatus())) {
            case WAITING:
                updateQueuePosition(queue);
                break;
            case ACTIVE:
                // 임시 배정된 좌석 정보 조회 (필요시 구현)
                break;
            case EXPIRED:
                // 만료된 큐 처리 (필요시 새로운 큐 생성 또는 예외 처리)
                throw new QueueExpiredException("대기열이 만료되었습니다.");
        }
        
        return new QueueStatusResponse(queue.getQueuePosition(), queue.getRemainingTimeInSeconds());
    }

    private Queue convertToQueue(TokenEntity tokenEntity) {
        Queue queue = new Queue();
        queue.setId(tokenEntity.getId());
        queue.setUserId(tokenEntity.getUserId());
        queue.setToken(tokenEntity.getToken());
        queue.setStatus(tokenEntity.getStatus());
        queue.setCreatedAt(tokenEntity.getCreatedAt());
        queue.setExpiresAt(tokenEntity.getExpiresAt());
        queue.setLastUpdatedAt(LocalDateTime.now());
        return queue;
    }

    private void updateQueuePosition(Queue queue) {
        List<QueueEntity> waitingQueues = queueRepository.findByStatusOrderByCreatedAt(Queue.QueueStatus.WAITING.name());
        int position = calculatePosition(waitingQueues, queue.getUserId());
        queue.setQueuePosition(position);
        saveQueue(queue);
    }

    private int calculatePosition(List<QueueEntity> waitingQueues, String userId) {
        for (int i = 0; i < waitingQueues.size(); i++) {
            if (waitingQueues.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return waitingQueues.size() + 1; // 리스트에 없으면 마지막 위치
    }

    private void saveQueue(Queue queue) {
        QueueEntity queueEntity = new QueueEntity();
        // Queue를 QueueEntity로 변환
        queueEntity.setId(queue.getId());
        queueEntity.setUserId(queue.getUserId());
        queueEntity.setToken(queue.getToken());
        queueEntity.setQueuePosition(queue.getQueuePosition());
        queueEntity.setStatus(queue.getStatus());
        queueEntity.setCreatedAt(queue.getCreatedAt());
        queueEntity.setExpiresAt(queue.getExpiresAt());
        queueEntity.setLastUpdatedAt(queue.getLastUpdatedAt());
        
        queueRepository.save(queueEntity);
    }

    // private int calculateQueuePosition(String userId) {
    //     // 대기열 위치 계산 로직
    // }

    // private int estimateWaitingTime(String userId) {
    //     // 대기 시간 추정 로직
    // }
}
