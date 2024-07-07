package io.hhplus.concert.reservation.application.service.facade;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.hhplus.concert.reservation.presentation.request.BalanceRequest;
import io.hhplus.concert.reservation.presentation.request.PaymentRequest;
import io.hhplus.concert.reservation.presentation.request.QueueRequest;
import io.hhplus.concert.reservation.presentation.request.ReservationDateRequest;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.request.UserTokenRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.PaymentResponse;
import io.hhplus.concert.reservation.presentation.response.QueueResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;
import io.hhplus.concert.reservation.presentation.response.SeatReservationResponse;
import io.hhplus.concert.reservation.presentation.response.UserTokenResponse;

@Component
public class ConcertFacade {

    public UserTokenResponse generateUserToken(UserTokenRequest request) {
        UserTokenResponse response = new UserTokenResponse();
        response.setToken(UUID.randomUUID().toString());
        response.setQueuePosition(1);
        response.setRemainingTime(300);
        return response;
    }

    public List<String> getAvailableDates() {
        return Arrays.asList("2024-07-03", "2024-07-04");
    }

    public List<ReservationResponse> getAvailableSeats(ReservationDateRequest request) {
        return Arrays.asList(
                new ReservationResponse(1, true),
                new ReservationResponse(2, false)
        );
    }

    public SeatReservationResponse reserveSeat(SeatReservationRequest request) {
        SeatReservationResponse response = new SeatReservationResponse();
        response.setReservationId(UUID.randomUUID().toString());
        response.setExpiresAt(System.currentTimeMillis() + 3600000); // 1 hour later
        return response;
    }

    public BalanceResponse chargeBalance(BalanceRequest request) {
        BalanceResponse response = new BalanceResponse();
        response.setNewBalance(200);
        return response;
    }

    public BalanceResponse getBalance(String userId) {
        BalanceResponse response = new BalanceResponse();
        response.setCurrentBalance(200);
        return response;
    }

    public PaymentResponse makePayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(UUID.randomUUID().toString());
        response.setStatus("success");
        return response;
    }

    public QueueResponse checkQueue(QueueRequest request) {
        QueueResponse response = new QueueResponse();
        response.setQueuePosition(1);
        response.setRemainingTime(300);
        return response;
    }
}
