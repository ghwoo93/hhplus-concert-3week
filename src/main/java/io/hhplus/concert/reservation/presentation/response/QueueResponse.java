package io.hhplus.concert.reservation.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueueResponse {
    private int queuePosition;
    private String status;
    private long remainingTime;
}

