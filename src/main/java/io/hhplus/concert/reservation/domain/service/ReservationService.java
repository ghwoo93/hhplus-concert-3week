package io.hhplus.concert.reservation.domain.service;

import java.time.LocalDate;
import io.hhplus.concert.reservation.domain.model.Reservation;

public interface ReservationService {
    Reservation reserveSeat(String concertId, int seatNumber, String userId, LocalDate performanceDate);
    Reservation getReservation(String reservationId);
    void updateReservationStatus(String reservationId, String status);
}