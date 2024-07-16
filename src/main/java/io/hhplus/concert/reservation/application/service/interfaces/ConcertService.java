package io.hhplus.concert.reservation.application.service.interfaces;

import java.util.List;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;

public interface ConcertService {
    List<ConcertDTO> getAllConcerts();
    List<SeatDTO> getSeatsByConcertId(String concertId);
}
