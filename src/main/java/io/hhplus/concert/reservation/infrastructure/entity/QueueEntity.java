package io.hhplus.concert.reservation.infrastructure.entity;

import java.time.LocalDateTime;

import io.hhplus.concert.reservation.domain.model.Queue;
import jakarta.persistence.Column;
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
@Table(name = "QUEUE")
@Getter @Setter
public class QueueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String token;
    
    @Column(nullable = false)
    private int queuePosition;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Queue.QueueStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
