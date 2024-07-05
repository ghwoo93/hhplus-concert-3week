package io.hhplus.concert.reservation.aggregate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSeat {
    private int seatNumber;
    private boolean available;
}
