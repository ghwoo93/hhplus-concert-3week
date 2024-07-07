package io.hhplus.concert.reservation.presentation.response;

import lombok.Data;

@Data
public class QueueResponse {
    private int queuePosition;
    private int remainingTime;
}
