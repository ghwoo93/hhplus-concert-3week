package io.hhplus.concert.reservation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConcertDTO {
    private String concertId;
    private String concertName;
    private String date;
}
