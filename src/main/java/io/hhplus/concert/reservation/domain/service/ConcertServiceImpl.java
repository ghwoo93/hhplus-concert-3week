package io.hhplus.concert.reservation.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.exception.ConcertNotFoundException;
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
    private static final Logger logger = LoggerFactory.getLogger(ConcertServiceImpl.class);
    
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public ConcertServiceImpl(ConcertRepository concertRepository, SeatRepository seatRepository) {
        this.concertRepository = concertRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<Concert> getAllConcerts() {
        List<ConcertEntity> concertEntities = concertRepository.findAll();
        return concertEntities.stream()
                .map(this::convertToConcert)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seat> getSeatsByConcertId(String concertId) {
        List<SeatEntity> seatEntities = seatRepository.findById_ConcertId(concertId);
        logger.debug("[getSeatsByConcertId] {} seats found for concertId: {}", seatEntities.size(), concertId);
        return seatEntities.stream()
                .map(SeatMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Concert getConcertById(String concertId) {
        return concertRepository.findById(concertId)
                .map(this::convertToConcert)
                .orElseThrow(() -> new ConcertNotFoundException("Concert not found with id: " + concertId));
    }

    private Concert convertToConcert(ConcertEntity concertEntity) {
        List<Seat> seats = getSeatsByConcertId(concertEntity.getId());
        return ConcertMapper.entityToDomain(concertEntity, seats);
    }
}
