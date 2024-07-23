package io.hhplus.concert.reservation.application.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
import io.hhplus.concert.reservation.domain.model.Concert;
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

        when(userService.existsById(userId)).thenReturn(true);
        when(tokenService.getOrCreateTokenForUser(userId)).thenReturn(newToken);

        TokenDTO result = concertFacade.issueToken(userId);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1, result.getQueuePosition());
        assertTrue(result.getRemainingTime() <= 3600 && result.getRemainingTime() > 3590,
                "Remaining time should be between 3590 and 3600 seconds, but was: " + result.getRemainingTime());

        verify(userService).existsById(userId);
        verify(tokenService).getOrCreateTokenForUser(userId);
    }

    @Test
    void getAllConcerts_ShouldReturnConcertDTOList() {
        Concert concert1 = new Concert();
        concert1.setId("1");
        concert1.setConcertName("Concert A");
        concert1.setDate(LocalDate.of(2023, 7, 14));

        Concert concert2 = new Concert();
        concert2.setId("2");
        concert2.setConcertName("Concert B");
        concert2.setDate(LocalDate.of(2023, 8, 20));

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
    
        when(concertService.getSeatsByConcertId(concertId)).thenReturn(Arrays.asList(
            new io.hhplus.concert.reservation.domain.model.Seat(),
            new io.hhplus.concert.reservation.domain.model.Seat()
        ));
    
        List<SeatDTO> result = concertFacade.getSeatsByConcertId(concertId);
    
        assertNotNull(result);
        assertEquals(2, result.size());
    
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
        LocalDate nowDate = LocalDate.now();
        Token validToken = new Token("user1");
        validToken.setStatus(TokenStatus.ACTIVE);
    
        Concert mockConcert = new Concert();
        mockConcert.setId("concert1");
        mockConcert.setDate(nowDate);
    
        Reservation mockReservation = new Reservation("reservation1", "user1", "concert1", 1, "TEMPORARY", now, nowDate);
    
        when(tokenService.getTokenStatus("user1")).thenReturn(validToken);
        when(concertService.getConcertById("concert1")).thenReturn(mockConcert);
        when(reservationService.reserveSeat(eq("concert1"), eq(1), eq("user1"), eq(nowDate)))
            .thenReturn(mockReservation);
    
        ReservationResponse result = concertFacade.reserveSeat(request);
    
        assertNotNull(result);
        assertEquals("reservation1", result.getReservationId());
        assertTrue(result.getExpiresAt().isAfter(now), "Expiration time should be in the future");
        assertTrue(result.getExpiresAt().isBefore(now.plusMinutes(6)), "Expiration time should be within 6 minutes");
    
        verify(tokenService).getTokenStatus("user1");
        verify(concertService).getConcertById("concert1");
        verify(reservationService).reserveSeat("concert1", 1, "user1", nowDate);
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
    
        Reservation reservation = new Reservation(reservationId, userId, "concert1", 1, "TEMPORARY", LocalDateTime.now(), LocalDate.now());
        User user = new User(userId, "username", "password", BigDecimal.valueOf(2000), LocalDateTime.now());
    
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(userService.getUser(userId)).thenReturn(user);
        when(paymentService.processPayment(anyString(), anyString(), any(BigDecimal.class))).thenReturn(expectedPaymentDTO);
    
        PaymentDTO result = concertFacade.processPayment(reservationId, amount, userId);
    
        assertEquals(expectedPaymentDTO, result);
        verify(reservationService).getReservation(reservationId);
        verify(userService).getUser(userId);
        verify(paymentService).processPayment(userId, reservationId, amount);
    }

    @Test
    void processPayment_InsufficientBalance() {
        String reservationId = "res1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String userId = "user1";
    
        Reservation reservation = new Reservation(reservationId, userId, "concert1", 1, "TEMPORARY", LocalDateTime.now(), LocalDate.now());
        User user = new User(userId, "username", "password", BigDecimal.valueOf(500), LocalDateTime.now());
    
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(userService.getUser(userId)).thenReturn(user);
    
        assertThrows(InsufficientBalanceException.class, () -> concertFacade.processPayment(reservationId, amount, userId));
        
        verify(reservationService).getReservation(reservationId);
        verify(userService).getUser(userId);
        verifyNoInteractions(paymentService);
    }
}