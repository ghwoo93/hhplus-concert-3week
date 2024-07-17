package io.hhplus.concert.reservation.application.service.facade;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.ConcertFacade;
import io.hhplus.concert.reservation.application.service.interfaces.ConcertService;
import io.hhplus.concert.reservation.application.service.interfaces.QueueService;
import io.hhplus.concert.reservation.application.service.interfaces.ReservationService;
import io.hhplus.concert.reservation.application.service.interfaces.TokenService;
import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;


@Service
public class ConcertFacadeImpl implements ConcertFacade {

    private final QueueService queueService;
    private final TokenService tokenService;
    private final ConcertService concertService;
    private final ReservationService reservationService;


    @Autowired
    public ConcertFacadeImpl(QueueService queueService, TokenService tokenService,
                            ConcertService concertService, ReservationService reservationService) {
        this.queueService = queueService;
        this.tokenService = tokenService;
        this.concertService = concertService;
        this.reservationService = reservationService;
    }

    @Override
    @Transactional
    public TokenDTO issueToken(String userId) {
        Queue queue = queueService.getOrCreateQueueForUser(userId);
        
        switch (queue.getStatus()) {
            case ACTIVE:
                Token token = tokenService.createToken(userId);
                return new TokenDTO(token.getToken(), "ACTIVE", queue.getQueuePosition(), queue.getRemainingTimeInSeconds());
            case WAITING:
                return new TokenDTO(null, "WAITING", queue.getQueuePosition(), queue.getRemainingTimeInSeconds());
            case EXPIRED:
                queue = queueService.createNewQueue(userId);
                Token newToken = tokenService.createToken(userId);
                return new TokenDTO(newToken.getToken(), "ACTIVE", queue.getQueuePosition(), queue.getRemainingTimeInSeconds());
            default:
                throw new IllegalStateException("Unexpected queue status");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QueueDTO checkQueueStatus(String token) {
        Queue queue = queueService.getQueueStatus(token);
        return new QueueDTO(queue.getQueuePosition(), queue.getStatus().name(), queue.getRemainingTimeInSeconds());
    }

    @Override
    @Transactional
    public QueueDTO createQueue(String userId) {
        Queue queue = queueService.createNewQueue(userId);
        return new QueueDTO(queue.getQueuePosition(), queue.getStatus().name(), queue.getRemainingTimeInSeconds());
    }

    @Override
    public List<ConcertDTO> getAllConcerts() {
        return concertService.getAllConcerts();
    }

    @Override
    public List<SeatDTO> getSeatsByConcertId(String concertId) {
        return concertService.getSeatsByConcertId(concertId);
    }

    @Override
    @Transactional
    public ReservationResponse reserveSeat(SeatReservationRequest request) {
        if (!tokenService.isTokenValid(request.getToken())) {
            throw new TokenNotFoundException();
        }

        Reservation reservation = reservationService.reserveSeat(
            request.getConcertId(), 
            request.getSeatNumber(), 
            request.getUserId()
        );

        // 만료 시간을 5분 후로 설정
        LocalDateTime expiresAt = reservation.getReservedAt().plusMinutes(5);

        return new ReservationResponse(reservation.getId(), expiresAt);
        // return new ReservationResponse(reservation.getId(), reservation.getReservedAt());
    }

}