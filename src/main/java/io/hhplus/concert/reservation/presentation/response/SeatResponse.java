package io.hhplus.concert.reservation.presentation.response;

import lombok.Data;

@Data
public class SeatResponse {
    private int seatNumber;
    private boolean available;
}
