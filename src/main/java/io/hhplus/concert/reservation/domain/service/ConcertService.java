package io.hhplus.concert.reservation.domain.service;

import java.util.List;

import io.hhplus.concert.reservation.domain.model.Concert;
import io.hhplus.concert.reservation.domain.model.Seat;

public interface ConcertService {
    List<Concert> getAllConcerts();
    List<Seat> getSeatsByConcertId(String concertId);
    Concert getConcertById(String concertId);
}
