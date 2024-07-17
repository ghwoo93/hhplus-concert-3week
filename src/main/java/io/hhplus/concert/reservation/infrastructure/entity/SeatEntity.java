package io.hhplus.concert.reservation.infrastructure.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "seats")
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String concertId;
    private int seatNumber;
    private boolean isReserved;
    private String reservedBy;
    private LocalDateTime reservedUntil;
}
