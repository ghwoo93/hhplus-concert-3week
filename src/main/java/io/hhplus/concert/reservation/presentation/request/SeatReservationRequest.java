package io.hhplus.concert.reservation.presentation.request;

import lombok.Data;

@Data
public class SeatReservationRequest {
    private String token;
    private String concertId;
    private int seatNumber;
    private String userId;
}
