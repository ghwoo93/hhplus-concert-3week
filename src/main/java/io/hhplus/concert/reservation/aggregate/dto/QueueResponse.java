package io.hhplus.concert.reservation.aggregate.dto;

import lombok.Data;

@Data
public class QueueResponse {
    private int queuePosition;
    private int remainingTime;
}
