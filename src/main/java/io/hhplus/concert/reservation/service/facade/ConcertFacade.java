package io.hhplus.concert.reservation.service.facade;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.hhplus.concert.reservation.aggregate.dto.BalanceRequest;
import io.hhplus.concert.reservation.aggregate.dto.BalanceResponse;
import io.hhplus.concert.reservation.aggregate.dto.PaymentRequest;
import io.hhplus.concert.reservation.aggregate.dto.PaymentResponse;
import io.hhplus.concert.reservation.aggregate.dto.QueueRequest;
import io.hhplus.concert.reservation.aggregate.dto.QueueResponse;
import io.hhplus.concert.reservation.aggregate.dto.ReservationDateRequest;
import io.hhplus.concert.reservation.aggregate.dto.ReservationSeat;
import io.hhplus.concert.reservation.aggregate.dto.SeatReservationRequest;
import io.hhplus.concert.reservation.aggregate.dto.SeatReservationResponse;
import io.hhplus.concert.reservation.aggregate.dto.UserTokenRequest;
import io.hhplus.concert.reservation.aggregate.dto.UserTokenResponse;

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

    public List<ReservationSeat> getAvailableSeats(ReservationDateRequest request) {
        return Arrays.asList(
                new ReservationSeat(1, true),
                new ReservationSeat(2, false)
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
