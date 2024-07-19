package io.hhplus.concert.reservation.domain.service;

import io.hhplus.concert.reservation.domain.model.Reservation;

public interface ReservationService {
    // ReservationDTO reserveSeat(String concertId, int seatNumber, String token);
    // List<ConcertDateResponse> getAvailableConcertDates();
    // List<SeatResponse> getAvailableSeats(String concertId);
    Reservation reserveSeat(String concertId, int seatNumber, String userId);
    Reservation getReservation(String reservationId);
    void updateReservationStatus(String reservationId, String status);
}