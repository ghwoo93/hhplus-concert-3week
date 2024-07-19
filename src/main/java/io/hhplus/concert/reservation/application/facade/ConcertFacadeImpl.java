package io.hhplus.concert.reservation.application.facade;

import io.hhplus.concert.reservation.application.dto.*;
import io.hhplus.concert.reservation.application.exception.*;
import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.domain.model.*;
import io.hhplus.concert.reservation.domain.service.*;
import io.hhplus.concert.reservation.infrastructure.mapper.TokenMapper;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ConcertFacadeImpl implements ConcertFacade {

    private final TokenService tokenService;
    private final ConcertService concertService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final UserService userService;

    public ConcertFacadeImpl(TokenService tokenService,
                             ConcertService concertService,
                             ReservationService reservationService,
                             PaymentService paymentService,
                             UserService userService) {
        this.tokenService = tokenService;
        this.concertService = concertService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public TokenDTO issueToken(String userId) {
        Token token = tokenService.getOrCreateTokenForUser(userId);
        return TokenMapper.toDto(token);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenDTO checkTokenStatus(String userId) {
        Token token = tokenService.getTokenStatus(userId);
        if (token.isExpired()) {
            tokenService.expireToken(token);
            throw new TokenExpiredException();
        }
        return TokenMapper.toDto(token);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenValid(String userId) {
        return tokenService.isTokenValid(userId);
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
        Token token = tokenService.getTokenStatus(request.getUserId());
        if (token.getStatus() != TokenStatus.ACTIVE) {
            throw new TokenInvalidStatusException();
        }

        Reservation reservation = reservationService.reserveSeat(
            request.getConcertId(), 
            request.getSeatNumber(), 
            request.getUserId()
        );

        return new ReservationResponse(reservation.getId(), reservation.getReservedAt().plusMinutes(5));
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
            throw new TokenInvalidStatusException();
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