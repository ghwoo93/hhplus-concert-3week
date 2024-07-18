package io.hhplus.concert.reservation.application.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.exception.InsufficientBalanceException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.domain.service.ConcertService;
import io.hhplus.concert.reservation.domain.service.PaymentService;
import io.hhplus.concert.reservation.domain.service.QueueService;
import io.hhplus.concert.reservation.domain.service.ReservationService;
import io.hhplus.concert.reservation.domain.service.TokenService;
import io.hhplus.concert.reservation.domain.service.UserService;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;

public class ConcertFacadeTest {
    @Mock
    private QueueService queueService;

    @Mock
    private TokenService tokenService;

    @Mock
    private ConcertService concertService;

    @Mock
    private ReservationService reservationService;
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private UserService userService;

    private ConcertFacade concertFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        concertFacade = new ConcertFacadeImpl(queueService, tokenService, concertService, 
                                            reservationService, paymentService, userService);
    }

    @Test
    void issueToken_WhenUserNotInQueue_ShouldCreateNewQueueAndIssueToken() {
        String userId = "user1";
        Queue newQueue = new Queue(userId);  // 생성자를 사용하여 Queue 객체 생성
        newQueue.setStatus(Queue.QueueStatus.ACTIVE);
        newQueue.updateQueuePosition(1);
        newQueue.setExpirationTime(3600); // 1시간 후 만료
    
        Token newToken = new Token();
        newToken.setToken("newToken");
    
        when(queueService.getOrCreateQueueForUser(userId)).thenReturn(newQueue);
        when(tokenService.createToken(userId)).thenReturn(newToken);
    
        TokenDTO result = concertFacade.issueToken(userId);
    
        assertNotNull(result);
        assertEquals("newToken", result.getToken());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 3600 && result.getRemainingTime() > 3590,
                "Remaining time should be between 3590 and 3600 seconds, but was: " + result.getRemainingTime());
    
        verify(queueService).getOrCreateQueueForUser(userId);
        verify(tokenService).createToken(userId);
    }
    
    @Test
    void issueToken_WhenUserQueueExpired_ShouldCreateNewQueueAndIssueToken() {
        String userId = "user3";
        Queue expiredQueue = new Queue(userId);
        expiredQueue.setStatus(Queue.QueueStatus.EXPIRED);
    
        Queue newQueue = new Queue(userId);
        newQueue.setStatus(Queue.QueueStatus.ACTIVE);
        newQueue.updateQueuePosition(1);
        newQueue.setExpirationTime(3600); // 1시간 후 만료
    
        Token newToken = new Token();
        newToken.setToken("newToken");
    
        when(queueService.getOrCreateQueueForUser(userId)).thenReturn(expiredQueue);
        when(queueService.createNewQueue(userId)).thenReturn(newQueue);
        when(tokenService.createToken(userId)).thenReturn(newToken);
    
        TokenDTO result = concertFacade.issueToken(userId);
    
        assertNotNull(result);
        assertEquals("newToken", result.getToken());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 3600 && result.getRemainingTime() > 3550,
                "Remaining time should be between 3550 and 3600 seconds, but was: " + result.getRemainingTime());
    
        verify(queueService).getOrCreateQueueForUser(userId);
        verify(queueService).createNewQueue(userId);
        verify(tokenService).createToken(userId);
    }
    
    @Test
    void issueToken_WhenUserInWaitingQueue_ShouldReturnWaitingStatus() {
        String userId = "user2";
        Queue waitingQueue = new Queue(userId);
        waitingQueue.setStatus(Queue.QueueStatus.WAITING);
        waitingQueue.updateQueuePosition(10);
        waitingQueue.setLastUpdatedAt(LocalDateTime.now().minusSeconds(1)); // Ensure that the last updated time is set correctly
    
        when(queueService.getOrCreateQueueForUser(userId)).thenReturn(waitingQueue);
    
        TokenDTO result = concertFacade.issueToken(userId);
    
        assertNotNull(result);
        assertNull(result.getToken());
        assertEquals("WAITING", result.getStatus());
        assertEquals(10, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 10 && result.getRemainingTime() > 0,
                "Remaining time should be between 0 and 10 seconds, but was: " + result.getRemainingTime());
    
        verify(queueService).getOrCreateQueueForUser(userId);
        verifyNoInteractions(tokenService);
    }

    @Test
    void getAllConcerts_ShouldReturnConcertDTOList() {
        ConcertDTO concert1 = new ConcertDTO("1", "Concert A", "2023-07-14");
        ConcertDTO concert2 = new ConcertDTO("2", "Concert B", "2023-08-20");

        when(concertService.getAllConcerts()).thenReturn(Arrays.asList(concert1, concert2));
    
        List<ConcertDTO> result = concertFacade.getAllConcerts();
    
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Concert A", result.get(0).getConcertName());
        assertEquals("Concert B", result.get(1).getConcertName());
    
        verify(concertService, times(1)).getAllConcerts();
    }

    @Test
    void getSeatsByConcertId_ShouldReturnSeatDTOList() {
        String concertId = "1";
        SeatDTO seat1 = new SeatDTO(1, true);
        SeatDTO seat2 = new SeatDTO(2, false);
    
        when(concertService.getSeatsByConcertId(concertId)).thenReturn(Arrays.asList(seat1, seat2));
    
        List<SeatDTO> result = concertFacade.getSeatsByConcertId(concertId);
    
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getSeatNumber());
        assertTrue(result.get(0).isAvailable());
        assertEquals(2, result.get(1).getSeatNumber());
        assertFalse(result.get(1).isAvailable());
    
        verify(concertService, times(1)).getSeatsByConcertId(concertId);
    }

    @Test
    void reserveSeat_ShouldReturnReservationResponse() {
        // Arrange
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("validToken");
        request.setConcertId("concert1");
        request.setSeatNumber(1);
        request.setUserId("user1");

        LocalDateTime now = LocalDateTime.now();

        when(tokenService.isTokenValid(anyString())).thenReturn(true);
        when(reservationService.reserveSeat(anyString(), anyInt(), anyString()))
            .thenReturn(new Reservation("reservation1", "user1", "concert1", 1, "TEMPORARY", now));

        // Act
        ReservationResponse result = concertFacade.reserveSeat(request);

        // Assert
        assertNotNull(result);
        assertEquals("reservation1", result.getReservationId());
    
        // 수정된 부분: 날짜 비교 로직 개선
        assertTrue(result.getExpiresAt().isAfter(now), "Expiration time should be in the future");
        assertTrue(result.getExpiresAt().isBefore(now.plusMinutes(6)), "Expiration time should be within 6 minutes");

        verify(tokenService).isTokenValid("validToken");
        verify(reservationService).reserveSeat("concert1", 1, "user1");
    }

    @Test
    void reserveSeat_WithInvalidToken_ShouldThrowTokenNotFoundException() {
        // Arrange
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("invalidToken");

        when(tokenService.isTokenValid(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(TokenNotFoundException.class, () -> concertFacade.reserveSeat(request));
        verify(tokenService).isTokenValid("invalidToken");
        verifyNoInteractions(reservationService);
    }

    @Test
    void rechargeBalance_Success() {
        String userId = "user1";
        int amount = 1000;
        BalanceResponse expectedResponse = new BalanceResponse(1000, 1000);
        when(userService.rechargeBalance(userId, BigDecimal.valueOf(amount))).thenReturn(expectedResponse);

        BalanceResponse result = concertFacade.rechargeBalance(userId, BigDecimal.valueOf(amount));

        assertEquals(expectedResponse, result);
        verify(userService).rechargeBalance(userId, BigDecimal.valueOf(amount));
    }

    @Test
    void rechargeBalance_UserNotFound() {
        String userId = "nonexistent";
        int amount = 1000;
        when(userService.rechargeBalance(userId, BigDecimal.valueOf(amount))).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> concertFacade.rechargeBalance(userId, BigDecimal.valueOf(amount)));
    }

    @Test
    void getBalance_Success() {
        String userId = "user1";
        BalanceResponse expectedResponse = new BalanceResponse(1000, 1000);
        when(userService.getBalance(userId)).thenReturn(expectedResponse);

        BalanceResponse result = concertFacade.getBalance(userId);

        assertEquals(expectedResponse, result);
        verify(userService).getBalance(userId);
    }

    @Test
    void getBalance_UserNotFound() {
        String userId = "nonexistent";
        when(userService.getBalance(userId)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> concertFacade.getBalance(userId));
    }

    @Test
    void processPayment_Success() {
        String reservationId = "res1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String token = "validToken";
        PaymentDTO expectedPaymentDTO = new PaymentDTO("payment1", "COMPLETED");

        when(tokenService.isTokenValid(token)).thenReturn(true);
        when(paymentService.processPayment(reservationId, token, amount)).thenReturn(expectedPaymentDTO);

        PaymentDTO result = concertFacade.processPayment(reservationId, amount, token);

        assertEquals(expectedPaymentDTO, result);
        verify(tokenService).isTokenValid(token);
        verify(paymentService).processPayment(reservationId, token, amount);
    }

    @Test
    void processPayment_InvalidToken() {
        String reservationId = "res1";
        int amount = 1000;
        String token = "invalidToken";

        when(tokenService.isTokenValid(token)).thenReturn(false);

        assertThrows(TokenNotFoundException.class, () -> concertFacade.processPayment(reservationId, BigDecimal.valueOf(amount), token));
    }

    @Test
    void processPayment_InsufficientBalance() {
        String reservationId = "res1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String token = "validToken";

        when(tokenService.isTokenValid(token)).thenReturn(true);
        when(paymentService.processPayment(reservationId, token, amount))
            .thenThrow(new InsufficientBalanceException());

        assertThrows(InsufficientBalanceException.class, () -> concertFacade.processPayment(reservationId, amount, token));
    }
}