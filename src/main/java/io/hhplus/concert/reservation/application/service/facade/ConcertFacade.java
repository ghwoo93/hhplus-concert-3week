package io.hhplus.concert.reservation.application.service.facade;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.dto.ReservationDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.service.interfaces.PaymentService;
import io.hhplus.concert.reservation.application.service.interfaces.QueueService;
import io.hhplus.concert.reservation.application.service.interfaces.ReservationService;
import io.hhplus.concert.reservation.application.service.interfaces.UserService;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.PaymentResponse;
import io.hhplus.concert.reservation.presentation.response.QueueStatusResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;


@Service
public class ConcertFacade {

    private final UserService userService;
    private final QueueService queueService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @Autowired
    public ConcertFacade(UserService userService, QueueService queueService, ReservationService reservationService, PaymentService paymentService) {
        this.userService = userService;
        this.queueService = queueService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public TokenDTO generateToken(String userId) {
        return userService.generateToken(userId);
    }

    public BalanceResponse rechargeBalance(String userId, BigDecimal amount) {
        return userService.rechargeBalance(userId, amount);
    }

    public BalanceResponse getBalance(String userId) {
        return userService.getBalance(userId);
    }

    public QueueStatusResponse getQueueStatus(String token) {
        QueueDTO queueDTO = queueService.getQueueStatus(token);
        return mapToQueueStatusResponse(queueDTO);
    }

    private QueueStatusResponse mapToQueueStatusResponse(QueueDTO queueDTO) {
        return new QueueStatusResponse(
                queueDTO.getQueuePosition(),
                queueDTO.getRemainingTime()
        );
    }

    public ReservationResponse reserveSeat(String concertId, int seatNumber, String token) {
        ReservationDTO reservationDTO = reservationService.reserveSeat(concertId, seatNumber, token);
        return mapToReservationResponse(reservationDTO);
    }

    private ReservationResponse mapToReservationResponse(ReservationDTO reservationDTO) {
        return new ReservationResponse(
                reservationDTO.getReservationId(),
                reservationDTO.getExpiresAt()
        );
    }

    public List<ConcertDateResponse> getAvailableConcertDates() {
        return reservationService.getAvailableConcertDates();
    }

    public List<SeatResponse> getAvailableSeats(String concertId) {
        return reservationService.getAvailableSeats(concertId);
    }

    public PaymentResponse processPayment(String reservationId, BigDecimal amount, String token) {
        PaymentDTO paymentDTO = paymentService.processPayment(reservationId, amount, token);
        return mapToPaymentResponse(paymentDTO);
    }

    private PaymentResponse mapToPaymentResponse(PaymentDTO paymentDTO) {
        return new PaymentResponse(
                paymentDTO.getPaymentId(),
                paymentDTO.getStatus()
        );
    }
}




