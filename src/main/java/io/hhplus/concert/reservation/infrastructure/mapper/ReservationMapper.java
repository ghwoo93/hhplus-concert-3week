package io.hhplus.concert.reservation.infrastructure.mapper;

import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.infrastructure.entity.ReservationEntity;

public class ReservationMapper {
    public static ReservationEntity toEntity(Reservation reservation) {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(reservation.getId());
        entity.setUserId(reservation.getUserId());
        entity.setConcertId(reservation.getConcertId());
        entity.setSeatNumber(reservation.getSeatNumber());
        entity.setReservationStatus(reservation.getReservationStatus());
        entity.setReservedAt(reservation.getReservedAt());
        entity.setPerformanceDate(reservation.getPerformanceDate());
        return entity;
    }

    public static Reservation toDomain(ReservationEntity entity) {
        return new Reservation(
            entity.getId(),
            entity.getUserId(),
            entity.getConcertId(),
            entity.getSeatNumber(),
            entity.getReservationStatus(),
            entity.getReservedAt(),
            entity.getPerformanceDate()
        );
    }
}
