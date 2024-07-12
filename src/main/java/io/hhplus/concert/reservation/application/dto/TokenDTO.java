package io.hhplus.concert.reservation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDTO {
    private String token;
    private String status;
    private int queuePosition;
    private long remainingTime;


}
