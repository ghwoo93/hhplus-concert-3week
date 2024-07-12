package io.hhplus.concert.reservation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDTO {
    private String token;
    private int queuePosition;
    private int remainingTime;
}
