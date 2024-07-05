package io.hhplus.concert.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert.reservation.aggregate.dto.*;
import io.hhplus.concert.reservation.service.facade.ConcertFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConcertController.class)
public class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacade concertFacade;

    @Autowired
    private ObjectMapper objectMapper;

    private UserTokenRequest userTokenRequest;
    private UserTokenResponse userTokenResponse;

    @BeforeEach
    void setUp() {
        userTokenRequest = new UserTokenRequest();
        userTokenRequest.setUserId("123e4567-e89b-12d3-a456-426614174000");

        userTokenResponse = new UserTokenResponse();
        userTokenResponse.setToken("mockToken");
        userTokenResponse.setQueuePosition(1);
        userTokenResponse.setRemainingTime(300);
    }

    @Test
    void testGenerateUserToken() throws Exception {
        when(concertFacade.generateUserToken(any(UserTokenRequest.class))).thenReturn(userTokenResponse);

        mockMvc.perform(post("/api/concert/v1/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"))
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(300));
    }

    @Test
    void testGetAvailableDates() throws Exception {
        when(concertFacade.getAvailableDates()).thenReturn(Arrays.asList("2024-07-03", "2024-07-04"));

        mockMvc.perform(get("/api/concert/v1/reservations/dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("2024-07-03"))
                .andExpect(jsonPath("$[1]").value("2024-07-04"));
    }

    @Test
    void testGetAvailableSeats() throws Exception {
        ReservationDateRequest dateRequest = new ReservationDateRequest();
        dateRequest.setDate("2024-07-03");

        ReservationSeat seat1 = new ReservationSeat();
        seat1.setSeatNumber(1);
        seat1.setAvailable(true);

        ReservationSeat seat2 = new ReservationSeat();
        seat2.setSeatNumber(2);
        seat2.setAvailable(false);

        when(concertFacade.getAvailableSeats(any(ReservationDateRequest.class)))
                .thenReturn(Arrays.asList(seat1, seat2));

        mockMvc.perform(get("/api/concert/v1/reservations/seats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].seatNumber").value(2))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    void testReserveSeat() throws Exception {
        SeatReservationRequest reservationRequest = new SeatReservationRequest();
        reservationRequest.setDate("2024-07-03");
        reservationRequest.setSeatNumber(1);
        reservationRequest.setToken("mockToken");

        SeatReservationResponse reservationResponse = new SeatReservationResponse();
        reservationResponse.setReservationId("123e4567-e89b-12d3-a456-426614174001");
        reservationResponse.setExpiresAt(System.currentTimeMillis() + 3600000);

        when(concertFacade.reserveSeat(any(SeatReservationRequest.class))).thenReturn(reservationResponse);

        mockMvc.perform(post("/api/concert/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value("123e4567-e89b-12d3-a456-426614174001"));
    }

    @Test
    void testChargeBalance() throws Exception {
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setUserId("123e4567-e89b-12d3-a456-426614174000");
        balanceRequest.setAmount(100);

        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setNewBalance(200);

        when(concertFacade.chargeBalance(any(BalanceRequest.class))).thenReturn(balanceResponse);

        mockMvc.perform(post("/api/concert/v1/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(balanceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalance").value(200));
    }

    @Test
    void testGetBalance() throws Exception {
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setCurrentBalance(200);

        when(concertFacade.getBalance(any(String.class))).thenReturn(balanceResponse);

        mockMvc.perform(get("/api/concert/v1/balance")
                .param("userId", "123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBalance").value(200));
    }

    @Test
    void testMakePayment() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId("123e4567-e89b-12d3-a456-426614174001");
        paymentRequest.setAmount(100);
        paymentRequest.setToken("mockToken");

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId("123e4567-e89b-12d3-a456-426614174002");
        paymentResponse.setStatus("success");

        when(concertFacade.makePayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/concert/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("123e4567-e89b-12d3-a456-426614174002"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testCheckQueue() throws Exception {
        QueueRequest queueRequest = new QueueRequest("mockToken");

        QueueResponse queueResponse = new QueueResponse();
        queueResponse.setQueuePosition(1);
        queueResponse.setRemainingTime(300);

        when(concertFacade.checkQueue(any(QueueRequest.class))).thenReturn(queueResponse);

        mockMvc.perform(get("/api/concert/v1/queue")
                .param("token", "mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(300));
    }
}
