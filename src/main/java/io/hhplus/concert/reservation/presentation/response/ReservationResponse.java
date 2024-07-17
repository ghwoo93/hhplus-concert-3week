package io.hhplus.concert.reservation.presentation.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {
    private String reservationId;
    private LocalDateTime expiresAt;
}
