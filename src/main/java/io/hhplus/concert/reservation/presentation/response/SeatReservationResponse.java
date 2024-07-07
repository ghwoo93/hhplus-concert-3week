package io.hhplus.concert.reservation.presentation.response;

import lombok.Data;

@Data
public class SeatReservationResponse {
    private String reservationId;
    private long expiresAt;
}
