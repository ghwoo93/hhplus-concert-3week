package io.hhplus.concert.reservation.aggregate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueueRequest {
    private String token;
}
