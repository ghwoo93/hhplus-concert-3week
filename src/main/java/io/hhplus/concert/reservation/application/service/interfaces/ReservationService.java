package io.hhplus.concert.reservation.application.service.interfaces;

import java.util.List;

import io.hhplus.concert.reservation.application.dto.ReservationDTO;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;

public interface ReservationService {
    ReservationDTO reserveSeat(String concertId, int seatNumber, String token);
    List<ConcertDateResponse> getAvailableConcertDates();
    List<SeatResponse> getAvailableSeats(String concertId);
}