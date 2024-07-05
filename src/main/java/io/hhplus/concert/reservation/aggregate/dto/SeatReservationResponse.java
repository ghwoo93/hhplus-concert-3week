package io.hhplus.concert.reservation.aggregate.dto;

import lombok.Data;

@Data
public class SeatReservationResponse {
    private String reservationId;
    private long expiresAt;
}
