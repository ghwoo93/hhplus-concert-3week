package io.hhplus.concert.reservation.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private int queuePosition;
    private long remainingTime;
}
