package io.hhplus.concert.reservation.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    Optional<SeatEntity> findByConcertIdAndSeatNumber(String concertId, int seatNumber);
    List<SeatEntity> findByConcertId(String concertId);
    List<SeatEntity> findByReservedUntilLessThan(LocalDateTime dateTime);
}