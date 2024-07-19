package io.hhplus.concert.reservation.domain.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Seat {
    private Long id;
    private String concertId;
    private int seatNumber;
    private boolean isReserved;
    private String reservedBy;
    private LocalDateTime reservedUntil;
}
