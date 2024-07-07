package io.hhplus.concert.reservation.presentation.response;

import lombok.Data;

@Data
public class UserTokenResponse {
    private String token;
    private int queuePosition;
    private int remainingTime;
}
