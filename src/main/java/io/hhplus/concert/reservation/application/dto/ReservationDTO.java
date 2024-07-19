package io.hhplus.concert.reservation.application.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReservationDTO {
    private String reservationId;
    private LocalDateTime expiresAt;
}
