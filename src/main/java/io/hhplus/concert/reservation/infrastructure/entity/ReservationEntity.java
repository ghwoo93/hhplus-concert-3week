package io.hhplus.concert.reservation.infrastructure.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reservations")
@Data
public class ReservationEntity {
    @Id
    private String id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "concert_id", nullable = false)
    private String concertId;
    
    @Column(name = "seat_number", nullable = false)
    private int seatNumber;
    
    @Column(name = "reservation_status", nullable = false)
    private String reservationStatus;
    
    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt;
    
    @Column(name = "performance_date", nullable = false)
    private LocalDate performanceDate;
}
