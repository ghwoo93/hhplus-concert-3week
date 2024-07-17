package io.hhplus.concert.reservation.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;

public class ResponseMapper {

    public static ConcertDateResponse toConcertDateResponse(ConcertDTO dto) {
        ConcertDateResponse response = new ConcertDateResponse();
        response.setConcertId(dto.getConcertId());
        response.setConcertName(dto.getConcertName());
        response.setDate(LocalDate.parse(dto.getDate()));  // String to LocalDate
        return response;
    }

    public static List<ConcertDateResponse> toConcertDateResponseList(List<ConcertDTO> dtos) {
        return dtos.stream()
                   .map(ResponseMapper::toConcertDateResponse)
                   .collect(Collectors.toList());
    }

    public static SeatResponse toSeatResponse(SeatDTO dto) {
        SeatResponse response = new SeatResponse();
        response.setSeatNumber(dto.getSeatNumber());
        response.setAvailable(dto.isAvailable());
        return response;
    }

    public static List<SeatResponse> toSeatResponseList(List<SeatDTO> dtos) {
        return dtos.stream()
                   .map(ResponseMapper::toSeatResponse)
                   .collect(Collectors.toList());
    }
}

