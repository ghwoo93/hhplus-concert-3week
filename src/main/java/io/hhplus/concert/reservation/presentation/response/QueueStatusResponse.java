package io.hhplus.concert.reservation.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueueStatusResponse {
    private int queuePosition;
    private long remainingTime;
}
