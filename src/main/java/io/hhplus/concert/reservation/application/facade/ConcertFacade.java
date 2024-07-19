package io.hhplus.concert.reservation.application.facade;

import java.math.BigDecimal;
import java.util.List;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;

public interface ConcertFacade {
    TokenDTO issueToken(String userId);
    TokenDTO checkTokenStatus(String userId);
    boolean isTokenValid(String userId);
    List<ConcertDTO> getAllConcerts();
    List<SeatDTO> getSeatsByConcertId(String concertId);
    ReservationResponse reserveSeat(SeatReservationRequest request);
    BalanceResponse rechargeBalance(String userId, BigDecimal amount);
    BalanceResponse getBalance(String userId);
    PaymentDTO processPayment(String reservationId, BigDecimal amount, String userId);
}