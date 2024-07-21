package io.hhplus.concert.reservation;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.concert.reservation.presentation.request.PaymentRequest;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.request.TokenRequest;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;
import io.hhplus.concert.reservation.presentation.response.TokenResponse;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testReservationFlow() throws Exception {
        // 1. 토큰 발급
        String userId = "user1";
        MvcResult tokenResult = mockMvc.perform(post("/api/v1/tokens")
                .header("X-API-Key", "a111111111111111111111111111111")  // API 키 추가
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TokenRequest(userId))))
                .andExpect(status().isCreated())
                .andReturn();
        
        
        TokenResponse tokenResponse = objectMapper.readValue(tokenResult.getResponse().getContentAsString(), TokenResponse.class);
        String token = tokenResponse.getToken().substring(7);
        // 2. 콘서트 조회
        MvcResult concertsResult = mockMvc.perform(get("/api/v1/reservations/dates")
                .header("X-API-Key", "a111111111111111111111111111111")  // API 키 추가
                .header("Authorization", "Bearer " + tokenResponse.getToken()))
                // .header("Authorization", tokenResponse.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        List<ConcertDateResponse> concerts = objectMapper.readValue(concertsResult.getResponse().getContentAsString(),
                new TypeReference<List<ConcertDateResponse>>() {});
        String concertId = concerts.get(0).getConcertId();

        // 3. 좌석 조회
        MvcResult seatsResult = mockMvc.perform(get("/api/v1/reservations/{concertId}/seats", concertId)
                .header("X-API-Key", "a111111111111111111111111111111")  // API 키 추가
                .header("Authorization", "Bearer " + tokenResponse.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        List<SeatResponse> seats = objectMapper.readValue(seatsResult.getResponse().getContentAsString(),
                new TypeReference<List<SeatResponse>>() {});
        // available한 좌석 중 첫 번째 좌석 선택
        int seatNumber = seats.stream()
                                .filter(SeatResponse::isAvailable)
                                .findFirst()
                                .map(SeatResponse::getSeatNumber)
                                .orElseThrow(() -> new RuntimeException("No available seats found"));

        // 4. 좌석 예약
        SeatReservationRequest reservationRequest = new SeatReservationRequest();
        reservationRequest.setToken(tokenResponse.getToken());
        reservationRequest.setConcertId(concertId);
        reservationRequest.setSeatNumber(seatNumber);
        reservationRequest.setUserId(userId);

        MvcResult reservationResult = mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationRequest))
                .header("X-API-Key", "a111111111111111111111111111111")  // API 키 추가
                .header("Authorization", "Bearer " + tokenResponse.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        ReservationResponse reservationResponse = objectMapper.readValue(reservationResult.getResponse().getContentAsString(), ReservationResponse.class);

        // 5. 결제
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId(reservationResponse.getReservationId());
        paymentRequest.setAmount(new BigDecimal("100.00"));
        paymentRequest.setToken(tokenResponse.getToken());

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest))
                .header("X-API-Key", "a111111111111111111111111111111")  // API 키 추가
                .header("Authorization", "Bearer " + tokenResponse.getToken()))
                .andExpect(status().isOk());
    }
}
