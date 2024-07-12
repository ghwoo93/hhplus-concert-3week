package io.hhplus.concert.reservation.infrastructure.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.hhplus.concert.reservation.domain.model.Queue.QueueStatus;
import io.hhplus.concert.reservation.infrastructure.entity.QueueEntity;

public interface QueueRepository extends JpaRepository<QueueEntity, Long> {
    List<QueueEntity> findByStatusOrderByCreatedAt(QueueStatus status);
    QueueEntity save(QueueEntity queue);    
}