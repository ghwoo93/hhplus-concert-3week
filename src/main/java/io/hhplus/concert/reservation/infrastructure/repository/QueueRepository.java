package io.hhplus.concert.reservation.infrastructure.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.infrastructure.entity.QueueEntity;

public interface QueueRepository extends JpaRepository<QueueEntity, Long> {
    Optional<QueueEntity> findByToken(String token);
    List<QueueEntity> findByStatusOrderByCreatedAt(Queue.QueueStatus status);
    List<QueueEntity> findByStatusOrderByCreatedAt(String status);
    Optional<QueueEntity> findByUserId(String userId);
    void save(Queue queue);
}