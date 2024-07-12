package io.hhplus.concert.reservation.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hhplus.concert.reservation.domain.model.Seat;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findByConcertIdAndSeatNumber(String concertId, int seatNumber);
    List<Seat> findByConcertId(String concertId);
}