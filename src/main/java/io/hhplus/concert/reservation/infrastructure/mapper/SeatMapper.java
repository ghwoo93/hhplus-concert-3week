package io.hhplus.concert.reservation.infrastructure.mapper;

import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.domain.enums.SeatStatus;
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
        seat.setConcertId(entity.getId().getConcertId());
        seat.setSeatNumber(entity.getId().getSeatNumber());
        seat.setReserved(entity.getId().getStatus() != SeatStatus.AVAILABLE);
        seat.setReservedBy(entity.getReservedBy());
        seat.setReservedUntil(entity.getReservedUntil());
        return seat;
    }

    public static SeatEntity domainToEntity(Seat domain) {
        SeatEntity.SeatId id = new SeatEntity.SeatId(
            domain.getConcertId(),
            domain.getSeatNumber(),
            domain.isReserved() ? SeatStatus.RESERVED : SeatStatus.AVAILABLE
        );

        return new SeatEntity(
            id,
            domain.getReservedBy(),
            domain.getReservedUntil()
        );
    }

    public static SeatEntity createAvailableSeatEntity(String concertId, int seatNumber) {
        SeatEntity.SeatId id = new SeatEntity.SeatId(
            concertId,
            seatNumber,
            SeatStatus.AVAILABLE
        );

        return new SeatEntity(id, null, null);
    }
}
