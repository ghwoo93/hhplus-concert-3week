package io.hhplus.concert.reservation.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Reservation {
    private String id;
    private String userId;
    private String concertId;
    private int seatNumber;
    private String reservationStatus;
    private LocalDateTime reservedAt;
}
