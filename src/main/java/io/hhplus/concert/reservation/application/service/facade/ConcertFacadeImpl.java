package io.hhplus.concert.reservation.application.service.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.service.interfaces.ConcertFacade;
import io.hhplus.concert.reservation.application.service.interfaces.QueueService;
import io.hhplus.concert.reservation.application.service.interfaces.TokenService;
import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.domain.model.Token;


@Service
public class ConcertFacadeImpl implements ConcertFacade {

    private final QueueService queueService;
    private final TokenService tokenService;
    // private final UserService userService;
    // private final ReservationService reservationService;
    // private final PaymentService paymentService;

    @Autowired
    public ConcertFacadeImpl(QueueService queueService, TokenService tokenService) {
        this.queueService = queueService;
        this.tokenService = tokenService;
    }
    // @Autowired
    // public ConcertFacadeImpl(UserService userService, QueueService queueService, TokenService tokenService,
    //                         ReservationService reservationService, PaymentService paymentService) {
    //     this.userService = userService;
    //     this.queueService = queueService;
    //     this.tokenService = tokenService;
    //     this.reservationService = reservationService;
    //     this.paymentService = paymentService;
    // }

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

    // public TokenDTO generateToken(String userId) {
    //     return userService.generateToken(userId);
    // }

    // public BalanceResponse rechargeBalance(String userId, BigDecimal amount) {
    //     return userService.rechargeBalance(userId, amount);
    // }

    // public BalanceResponse getBalance(String userId) {
    //     return userService.getBalance(userId);
    // }

    // public QueueStatusResponse getQueueStatus(String token) {
    //     QueueDTO queueDTO = queueService.getQueueStatus(token);
    //     return mapToQueueStatusResponse(queueDTO);
    // }

    // private QueueStatusResponse mapToQueueStatusResponse(QueueDTO queueDTO) {
    //     return new QueueStatusResponse(
    //             queueDTO.getQueuePosition(),
    //             queueDTO.getRemainingTime()
    //     );
    // }

    // public ReservationResponse reserveSeat(String concertId, int seatNumber, String token) {
    //     ReservationDTO reservationDTO = reservationService.reserveSeat(concertId, seatNumber, token);
    //     return mapToReservationResponse(reservationDTO);
    // }

    // private ReservationResponse mapToReservationResponse(ReservationDTO reservationDTO) {
    //     return new ReservationResponse(
    //             reservationDTO.getReservationId(),
    //             reservationDTO.getExpiresAt()
    //     );
    // }

    // public List<ConcertDateResponse> getAvailableConcertDates() {
    //     return reservationService.getAvailableConcertDates();
    // }

    // public List<SeatResponse> getAvailableSeats(String concertId) {
    //     return reservationService.getAvailableSeats(concertId);
    // }

    // public PaymentResponse processPayment(String reservationId, BigDecimal amount, String token) {
    //     PaymentDTO paymentDTO = paymentService.processPayment(reservationId, amount, token);
    //     return mapToPaymentResponse(paymentDTO);
    // }

    // private PaymentResponse mapToPaymentResponse(PaymentDTO paymentDTO) {
    //     return new PaymentResponse(
    //             paymentDTO.getPaymentId(),
    //             paymentDTO.getStatus()
    //     );
    // }
}




