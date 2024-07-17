package io.hhplus.concert.reservation.application.service.interfaces;

import io.hhplus.concert.reservation.domain.model.Reservation;

public interface ReservationService {
    // ReservationDTO reserveSeat(String concertId, int seatNumber, String token);
    // List<ConcertDateResponse> getAvailableConcertDates();
    // List<SeatResponse> getAvailableSeats(String concertId);
    Reservation reserveSeat(String concertId, int seatNumber, String userId);
}