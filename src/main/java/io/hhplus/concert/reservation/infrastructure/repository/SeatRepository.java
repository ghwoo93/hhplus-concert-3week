package io.hhplus.concert.reservation.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.hhplus.concert.reservation.domain.enums.SeatStatus;
import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, SeatEntity.SeatId> {
    Optional<SeatEntity> findByConcertIdAndSeatNumber(String concertId, int seatNumber);
    List<SeatEntity> findByConcertId(String concertId);
    List<SeatEntity> findByReservedUntilLessThan(LocalDateTime dateTime);

    List<SeatEntity> findByIdStatusAndReservedUntilLessThan(SeatStatus status, LocalDateTime dateTime);
    @Modifying
    @Query("UPDATE SeatEntity s SET s.id.status = :newStatus, s.reservedBy = :userId, s.reservedUntil = :reservedUntil " +
           "WHERE s.id.concertId = :concertId AND s.id.seatNumber = :seatNumber AND s.id.status = :currentStatus")
    int updateSeatStatus(@Param("concertId") String concertId,
                         @Param("seatNumber") Integer seatNumber,
                         @Param("currentStatus") SeatStatus currentStatus,
                         @Param("newStatus") SeatStatus newStatus,
                         @Param("userId") String userId,
                         @Param("reservedUntil") LocalDateTime reservedUntil);
}