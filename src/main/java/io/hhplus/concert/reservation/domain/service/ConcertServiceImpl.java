package io.hhplus.concert.reservation.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.domain.model.Concert;
import io.hhplus.concert.reservation.domain.model.Seat;
import io.hhplus.concert.reservation.infrastructure.entity.ConcertEntity;
import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;
import io.hhplus.concert.reservation.infrastructure.mapper.ConcertMapper;
import io.hhplus.concert.reservation.infrastructure.mapper.SeatMapper;
import io.hhplus.concert.reservation.infrastructure.repository.ConcertRepository;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;

@Service
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public ConcertServiceImpl(ConcertRepository concertRepository, SeatRepository seatRepository) {
        this.concertRepository = concertRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<ConcertDTO> getAllConcerts() {
        List<ConcertEntity> concertEntities = concertRepository.findAll();
        return concertEntities.stream()
                .map(this::convertToConcertDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatDTO> getSeatsByConcertId(String concertId) {
        List<SeatEntity> seatEntities = seatRepository.findByConcertId(concertId);
        return seatEntities.stream()
                .map(this::convertToSeatDTO)
                .collect(Collectors.toList());
    }

    private ConcertDTO convertToConcertDTO(ConcertEntity concertEntity) {
        Concert concert = ConcertMapper.entityToDomain(concertEntity, null);
        return ConcertMapper.domainToDto(concert);
    }

    private SeatDTO convertToSeatDTO(SeatEntity seatEntity) {
        Seat seat = SeatMapper.entityToDomain(seatEntity);
        return SeatMapper.domainToDto(seat);
    }
}
