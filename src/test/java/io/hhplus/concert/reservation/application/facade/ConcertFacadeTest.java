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
import io.hhplus.concert.reservation.application.exception.TokenInvalidStatusException;
import io.hhplus.concert.reservation.domain.model.Reservation;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.domain.model.User;
import io.hhplus.concert.reservation.domain.service.ConcertService;
import io.hhplus.concert.reservation.domain.service.PaymentService;
import io.hhplus.concert.reservation.domain.service.ReservationService;
import io.hhplus.concert.reservation.domain.service.TokenService;
import io.hhplus.concert.reservation.domain.service.UserService;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;
import io.hhplus.concert.reservation.domain.enums.TokenStatus;

public class ConcertFacadeTest {
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
        concertFacade = new ConcertFacadeImpl(tokenService, concertService, 
                                            reservationService, paymentService, userService);
    }

    @Test
    void issueToken_WhenUserNotInQueue_ShouldCreateNewTokenAndIssueToken() {
        String userId = "user1";
        Token newToken = new Token(userId);
        newToken.setStatus(TokenStatus.ACTIVE);
        newToken.updateQueuePosition(1);
        newToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenService.getOrCreateTokenForUser(userId)).thenReturn(newToken);

        TokenDTO result = concertFacade.issueToken(userId);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 3600 && result.getRemainingTime() > 3590,
                "Remaining time should be between 3590 and 3600 seconds, but was: " + result.getRemainingTime());

        verify(tokenService).getOrCreateTokenForUser(userId);
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
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("validToken");
        request.setConcertId("concert1");
        request.setSeatNumber(1);
        request.setUserId("user1");
    
        LocalDateTime now = LocalDateTime.now();
        Token validToken = new Token("user1");
        validToken.setStatus(TokenStatus.ACTIVE);
    
        when(tokenService.getTokenStatus("user1")).thenReturn(validToken);
        when(reservationService.reserveSeat(anyString(), anyInt(), anyString()))
            .thenReturn(new Reservation("reservation1", "user1", "concert1", 1, "TEMPORARY", now));
    
        ReservationResponse result = concertFacade.reserveSeat(request);
    
        assertNotNull(result);
        assertEquals("reservation1", result.getReservationId());
        assertTrue(result.getExpiresAt().isAfter(now), "Expiration time should be in the future");
        assertTrue(result.getExpiresAt().isBefore(now.plusMinutes(6)), "Expiration time should be within 6 minutes");
    
        verify(tokenService).getTokenStatus("user1");
        verify(reservationService).reserveSeat("concert1", 1, "user1");
    }

    @Test
    void reserveSeat_WithInvalidToken_ShouldThrowTokenInvalidStatusException() {
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("invalidToken");
        request.setUserId("user1");
    
        Token invalidToken = new Token("user1");
        invalidToken.setStatus(TokenStatus.EXPIRED);
    
        when(tokenService.getTokenStatus("user1")).thenReturn(invalidToken);
    
        assertThrows(TokenInvalidStatusException.class, () -> concertFacade.reserveSeat(request));
        verify(tokenService).getTokenStatus("user1");
        verifyNoInteractions(reservationService);
    }

    @Test
    void rechargeBalance_Success() {
        String userId = "user1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        BalanceResponse expectedResponse = new BalanceResponse(1000, 1000);
        when(userService.rechargeBalance(userId, amount)).thenReturn(expectedResponse);

        BalanceResponse result = concertFacade.rechargeBalance(userId, amount);

        assertEquals(expectedResponse, result);
        verify(userService).rechargeBalance(userId, amount);
    }

    @Test
    void processPayment_Success() {
        String reservationId = "res1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String userId = "user1";
        PaymentDTO expectedPaymentDTO = new PaymentDTO("payment1", "COMPLETED");
    
        Reservation reservation = new Reservation(reservationId, userId, "concert1", 1, "TEMPORARY", LocalDateTime.now());
        User user = new User(userId, "username", "password", BigDecimal.valueOf(2000), LocalDateTime.now());
    
        when(tokenService.isTokenValid(userId)).thenReturn(true);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(userService.getUser(userId)).thenReturn(user);
        when(paymentService.processPayment(anyString(), anyString(), any(BigDecimal.class))).thenReturn(expectedPaymentDTO);
    
        PaymentDTO result = concertFacade.processPayment(reservationId, amount, userId);
    
        assertEquals(expectedPaymentDTO, result);
        verify(tokenService).isTokenValid(userId);
        verify(reservationService).getReservation(reservationId);
        verify(userService).getUser(userId);
        verify(paymentService).processPayment(userId, reservationId, amount);
    }

    @Test
    void processPayment_InvalidToken() {
        String reservationId = "res1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String token = "invalidToken";

        when(tokenService.isTokenValid(token)).thenReturn(false);

        assertThrows(TokenInvalidStatusException.class, () -> concertFacade.processPayment(reservationId, amount, token));
    }

    @Test
    void processPayment_InsufficientBalance() {
        String reservationId = "res1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String userId = "user1";
    
        Reservation reservation = new Reservation(reservationId, userId, "concert1", 1, "TEMPORARY", LocalDateTime.now());
        User user = new User(userId, "username", "password", BigDecimal.valueOf(500), LocalDateTime.now());
    
        when(tokenService.isTokenValid(userId)).thenReturn(true);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(userService.getUser(userId)).thenReturn(user);
        when(userService.deductBalance(eq(userId), any(BigDecimal.class)))
            .thenThrow(new InsufficientBalanceException());
    
        assertThrows(InsufficientBalanceException.class, () -> concertFacade.processPayment(reservationId, amount, userId));
        
        verify(tokenService).isTokenValid(userId);
        verify(reservationService).getReservation(reservationId);
        verify(userService).getUser(userId);
        verify(userService).deductBalance(eq(userId), any(BigDecimal.class));
        verifyNoInteractions(paymentService);
    }
}