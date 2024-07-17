package io.hhplus.concert.reservation.infrastructure.mapper;

import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.domain.model.Seat;
import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;

public class SeatMapper {
    public static Seat dtoToDomain(SeatDTO dto, String concertId) {
        Seat seat = new Seat();
        seat.setConcertId(concertId);
        seat.setSeatNumber(dto.getSeatNumber());
        seat.setReserved(!dto.isAvailable());
        return seat;
    }

    public static SeatDTO domainToDto(Seat domain) {
        return new SeatDTO(domain.getSeatNumber(), !domain.isReserved());
    }

    public static Seat entityToDomain(SeatEntity entity) {
        Seat seat = new Seat();
        seat.setId(entity.getId());
        seat.setConcertId(entity.getConcertId());
        seat.setSeatNumber(entity.getSeatNumber());
        seat.setReserved(entity.isReserved());
        seat.setReservedBy(entity.getReservedBy());
        seat.setReservedUntil(entity.getReservedUntil());
        return seat;
    }

    public static SeatEntity domainToEntity(Seat domain) {
        SeatEntity entity = new SeatEntity();
        entity.setId(domain.getId());
        entity.setConcertId(domain.getConcertId());
        entity.setSeatNumber(domain.getSeatNumber());
        entity.setReserved(domain.isReserved());
        entity.setReservedBy(domain.getReservedBy());
        entity.setReservedUntil(domain.getReservedUntil());
        return entity;
    }
}
