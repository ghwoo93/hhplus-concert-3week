package io.hhplus.concert.reservation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QueueDTO {
    private String token;
    private int queuePosition;
    private int remainingTime;
}
