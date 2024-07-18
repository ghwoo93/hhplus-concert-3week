package io.hhplus.concert.reservation.infrastructure.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seats")
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String concertId;

    @Column(nullable = false)
    private int seatNumber;

    @Column(nullable = false)
    private boolean isReserved;

    @Column(nullable = false)
    private String reservedBy;

    @Column(nullable = false)
    private LocalDateTime reservedUntil;

    public void reserve(String userId) {
        this.isReserved = true;
        this.reservedBy = userId;
        this.reservedUntil = LocalDateTime.now().plusMinutes(5);
    }
}
