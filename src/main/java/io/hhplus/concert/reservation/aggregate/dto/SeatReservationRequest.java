package io.hhplus.concert.reservation.aggregate.dto;

import lombok.Data;

@Data
public class SeatReservationRequest {
    private String date;
    private int seatNumber;
    private String token;
}
