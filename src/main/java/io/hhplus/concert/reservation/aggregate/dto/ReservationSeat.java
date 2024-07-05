package io.hhplus.concert.reservation.aggregate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationSeat {
    private int seatNumber;
    private boolean available;
}
