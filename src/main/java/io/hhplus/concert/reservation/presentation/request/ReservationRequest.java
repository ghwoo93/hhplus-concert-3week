package io.hhplus.concert.reservation.presentation.request;

import lombok.Data;

@Data
public class ReservationRequest {
    private String concertId;
    private int seatNumber;
    private String token;
}