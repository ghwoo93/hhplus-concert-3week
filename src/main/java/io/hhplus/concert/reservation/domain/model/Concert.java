package io.hhplus.concert.reservation.domain.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class Concert {
    private String id;
    private String concertName;
    private LocalDate date;
    private List<Seat> seats;
}
