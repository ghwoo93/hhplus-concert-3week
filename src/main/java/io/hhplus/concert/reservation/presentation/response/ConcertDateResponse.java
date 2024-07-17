package io.hhplus.concert.reservation.presentation.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ConcertDateResponse {
    private String concertId;
    private String concertName;
    private LocalDate date;
}
