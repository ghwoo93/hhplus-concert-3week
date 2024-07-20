package io.hhplus.concert.reservation.infrastructure.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.hhplus.concert.reservation.domain.enums.SeatStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity {
    @EmbeddedId
    private SeatId id;

    @Column(name = "reserved_by")
    private String reservedBy;

    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatId implements Serializable {
        @Column(name = "concert_id")
        private String concertId;

        @Column(name = "seat_number")
        private Integer seatNumber;

        @Column(name = "status")
        @Enumerated(EnumType.STRING)
        private SeatStatus status;
    }
}