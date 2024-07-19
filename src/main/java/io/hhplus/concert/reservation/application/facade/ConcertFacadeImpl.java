package io.hhplus.concert.reservation.application.facade;

import io.hhplus.concert.reservation.application.dto.*;
import io.hhplus.concert.reservation.application.exception.*;
import io.hhplus.concert.reservation.domain.model.*;
import io.hhplus.concert.reservation.domain.service.*;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConcertFacadeImpl implements ConcertFacade {

    private final QueueService queueService;
    private final TokenService tokenService;
    private final ConcertService concertService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final UserService userService;

    public ConcertFacadeImpl(QueueService queueService, TokenService tokenService,
                             ConcertService concertService, ReservationService reservationService,
                             PaymentService paymentService, UserService userService) {
        this.queueService = queueService;
        this.tokenService = tokenService;
        this.concertService = concertService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public TokenDTO issueToken(String userId) {
        Queue queue = queueService.getOrCreateQueueForUser(userId);
        return handleQueueStatus(queue, userId);
    }

    private TokenDTO handleQueueStatus(Queue queue, String userId) {
        switch (queue.getStatus()) {
            case ACTIVE:
                Token token = tokenService.createToken(userId);
                return new TokenDTO(token.getToken(), "ACTIVE", queue.getQueuePosition(), queue.getRemainingTimeInSeconds());
            case WAITING:
                return new TokenDTO(null, "WAITING", queue.getQueuePosition(), queue.getRemainingTimeInSeconds());
            case EXPIRED:
                Queue newQueue = queueService.createNewQueue(userId);
                Token newToken = tokenService.createToken(userId);
                return new TokenDTO(newToken.getToken(), "ACTIVE", newQueue.getQueuePosition(), newQueue.getRemainingTimeInSeconds());
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

        LocalDateTime expiresAt = reservation.getReservedAt().plusMinutes(5);
        return new ReservationResponse(reservation.getId(), expiresAt);
    }

    @Override
    @Transactional
    public BalanceResponse rechargeBalance(String userId, BigDecimal amount) {
        return userService.rechargeBalance(userId, amount);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String userId) {
        return userService.getBalance(userId);
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(String reservationId, BigDecimal amount, String token) {
        if (!tokenService.isTokenValid(token)) {
            throw new TokenNotFoundException();
        }

        Reservation reservation = reservationService.getReservation(reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException();
        }

        User user = userService.getUser(reservation.getUserId());
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        // 잔액 차감
        BalanceResponse balanceResponse = userService.deductBalance(user.getId(), amount);

        // 결제 처리
        PaymentDTO paymentDTO = paymentService.processPayment(user.getId(), reservationId, amount);

        // 예약 상태 업데이트
        reservationService.updateReservationStatus(reservationId, "COMPLETED");

        return paymentDTO;
    }
}