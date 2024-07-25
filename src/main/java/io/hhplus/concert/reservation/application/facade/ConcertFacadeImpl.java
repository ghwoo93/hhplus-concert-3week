package io.hhplus.concert.reservation.application.facade;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.exception.InsufficientBalanceException;
import io.hhplus.concert.reservation.application.exception.ReservationNotFoundException;
import io.hhplus.concert.reservation.application.exception.TokenExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenInvalidStatusException;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.domain.model.Concert;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.domain.model.User;
import io.hhplus.concert.reservation.domain.service.ConcertService;
import io.hhplus.concert.reservation.domain.service.PaymentService;
import io.hhplus.concert.reservation.domain.service.ReservationService;
import io.hhplus.concert.reservation.domain.service.TokenService;
import io.hhplus.concert.reservation.domain.service.UserService;
import io.hhplus.concert.reservation.infrastructure.entity.ConcertEntity;
import io.hhplus.concert.reservation.infrastructure.mapper.ConcertMapper;
import io.hhplus.concert.reservation.infrastructure.mapper.SeatMapper;
import io.hhplus.concert.reservation.infrastructure.mapper.TokenMapper;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;

@Service
public class ConcertFacadeImpl implements ConcertFacade {

    private static final Logger logger = LoggerFactory.getLogger(ConcertFacadeImpl.class);
    
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
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException();
        }
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
        List<Concert> concertEntities = concertService.getAllConcerts();
        return concertEntities.stream()
                .map(ConcertMapper::domainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatDTO> getSeatsByConcertId(String concertId) {
        return concertService.getSeatsByConcertId(concertId).stream()
                        .map(SeatMapper::domainToDto)
                        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse reserveSeat(SeatReservationRequest request) {
        Token token = tokenService.getTokenStatus(request.getUserId());
        if (token.getStatus() != TokenStatus.ACTIVE) {
            throw new TokenInvalidStatusException("Token is not active for user: " + request.getUserId());
        }

        Concert concert = concertService.getConcertById(request.getConcertId());

        Reservation reservation = reservationService.reserveSeat(
            request.getConcertId(),
            request.getSeatNumber(),
            request.getUserId(),
            concert.getDate()
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
        // boolean isTokenValid = tokenService.isTokenValid(token);
        // logger.debug("isTokenValid: {}", isTokenValid);
        // if (!isTokenValid) {
        //     throw new TokenInvalidStatusException("Token is not valid for payment processing for reservation: " + reservationId);
        // }

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