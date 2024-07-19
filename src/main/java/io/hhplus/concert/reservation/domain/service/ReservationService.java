package io.hhplus.concert.reservation.domain.service;

import io.hhplus.concert.reservation.domain.model.Reservation;

public interface ReservationService {
    Reservation reserveSeat(String concertId, int seatNumber, String userId);
    Reservation getReservation(String reservationId);
    void updateReservationStatus(String reservationId, String status);
}