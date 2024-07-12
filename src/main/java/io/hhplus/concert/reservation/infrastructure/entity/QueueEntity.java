package io.hhplus.concert.reservation.infrastructure.entity;

import java.time.LocalDateTime;

import io.hhplus.concert.reservation.domain.model.Queue.QueueStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "queue")
@Getter @Setter
public class QueueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userId;
    private int queuePosition;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    
    @Enumerated(EnumType.STRING)
    private QueueStatus status;
}
