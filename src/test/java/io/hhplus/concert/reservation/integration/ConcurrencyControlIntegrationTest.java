package io.hhplus.concert.reservation.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.concert.reservation.presentation.request.PaymentRequest;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.request.TokenRequest;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;
import io.hhplus.concert.reservation.presentation.response.TokenResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class ConcurrencyControlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int CONCURRENT_USERS = 10;
    private static final String API_KEY = "a111111111111111111111111111111";
    private static final String CONCERT_ID = "concert1";
    private static final int SEAT_NUMBER = 1;

    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
    }

    @Test
    public void testConcurrentReservationFlow() throws Exception {
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        List<Future<ReservationResponse>> futures = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final String userId = "user" + i;
            futures.add(executorService.submit(() -> {
                try {
                    TokenResponse tokenResponse = issueToken(userId);
                    ReservationResponse reservationResponse = reserveSeat(tokenResponse.getToken(), userId);
                    processPayment(reservationResponse.getReservationId(), tokenResponse.getToken());
                    return reservationResponse;
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();

        int successfulReservations = 0;
        for (Future<ReservationResponse> future : futures) {
            try {
                ReservationResponse response = future.get();
                if (response != null) {
                    successfulReservations++;
                }
            } catch (Exception e) {
                // 예약 실패
            }
        }

        assertEquals(1, successfulReservations, "Only one reservation should be successful");

        // 좌석 상태 확인
        List<SeatResponse> seats = getAvailableSeats();
        long reservedSeats = seats.stream().filter(seat -> !seat.isAvailable()).count();
        assertEquals(1, reservedSeats, "Only one seat should be reserved");
    }

    private TokenResponse issueToken(String userId) throws Exception {
        MvcResult tokenResult = mockMvc.perform(post("/api/v1/tokens")
                .header("X-API-Key", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TokenRequest(userId))))
                .andExpect(status().isCreated())
                .andReturn();
        
        return objectMapper.readValue(tokenResult.getResponse().getContentAsString(), TokenResponse.class);
    }

    private ReservationResponse reserveSeat(String token, String userId) throws Exception {
        SeatReservationRequest reservationRequest = new SeatReservationRequest();
        reservationRequest.setToken(token);
        reservationRequest.setConcertId(CONCERT_ID);
        reservationRequest.setSeatNumber(SEAT_NUMBER);
        reservationRequest.setUserId(userId);

        MvcResult reservationResult = mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationRequest))
                .header("X-API-Key", API_KEY)
                .header("Authorization", "Bearer " + token))
                .andReturn();

        if (reservationResult.getResponse().getStatus() == 200) {
            return objectMapper.readValue(reservationResult.getResponse().getContentAsString(), ReservationResponse.class);
        }
        return null;
    }

    private void processPayment(String reservationId, String token) throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId(reservationId);
        paymentRequest.setAmount(new BigDecimal("100.00"));
        paymentRequest.setToken(token);

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest))
                .header("X-API-Key", API_KEY)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private List<SeatResponse> getAvailableSeats() throws Exception {
        MvcResult seatsResult = mockMvc.perform(get("/api/v1/reservations/{concertId}/seats", CONCERT_ID)
                .header("X-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(seatsResult.getResponse().getContentAsString(),
                new TypeReference<List<SeatResponse>>() {});
    }
}