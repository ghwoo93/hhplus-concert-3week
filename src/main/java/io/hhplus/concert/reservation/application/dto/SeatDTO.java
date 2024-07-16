package io.hhplus.concert.reservation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatDTO {
    private int seatNumber;
    private boolean available;
}
