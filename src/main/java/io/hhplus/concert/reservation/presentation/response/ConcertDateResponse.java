package io.hhplus.concert.reservation.presentation.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ConcertDateResponse {
    private String concertId;
    private String concertName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
