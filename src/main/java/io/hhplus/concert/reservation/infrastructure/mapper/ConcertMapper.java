package io.hhplus.concert.reservation.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.domain.model.Concert;
import io.hhplus.concert.reservation.domain.model.Seat;
import io.hhplus.concert.reservation.infrastructure.entity.ConcertEntity;

public class ConcertMapper {
    public static Concert dtoToDomain(ConcertDTO dto) {
        Concert concert = new Concert();
        concert.setId(dto.getConcertId());
        concert.setConcertName(dto.getConcertName());
        concert.setDate(LocalDate.parse(dto.getDate())); // String to LocalDate
        return concert;
    }

    public static ConcertDTO domainToDto(Concert domain) {
        return new ConcertDTO(domain.getId(), domain.getConcertName(), domain.getDate().toString());
    }

    public static Concert entityToDomain(ConcertEntity entity, List<Seat> seats) {
        Concert concert = new Concert();
        concert.setId(entity.getId());
        concert.setConcertName(entity.getConcertName());
        concert.setDate(entity.getDate());
        concert.setSeats(seats);
        return concert;
    }

    public static ConcertEntity domainToEntity(Concert domain) {
        ConcertEntity entity = new ConcertEntity();
        entity.setId(domain.getId());
        entity.setConcertName(domain.getConcertName());
        entity.setDate(domain.getDate());
        return entity;
    }
}
