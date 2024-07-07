package io.hhplus.concert.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert.reservation.aggregate.dto.*;
import io.hhplus.concert.reservation.application.service.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.controller.ConcertController;
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

    /**
     * 유저 토큰 발급 API 테스트
     */
    @Test
    void testGenerateUserToken() throws Exception {
        // given: ConcertFacade의 generateUserToken 메서드가 userTokenResponse를 반환하도록 설정
        when(concertFacade.generateUserToken(any(UserTokenRequest.class))).thenReturn(userTokenResponse);

        // when: /api/concert/v1/token 엔드포인트에 POST 요청을 보냄
        mockMvc.perform(post("/api/concert/v1/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userTokenRequest)))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 값이 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"))
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(300));
    }

    /**
     * 예약 가능 날짜 조회 API 테스트
     */
    @Test
    void testGetAvailableDates() throws Exception {
        // given: ConcertFacade의 getAvailableDates 메서드가 예약 가능 날짜 리스트를 반환하도록 설정
        when(concertFacade.getAvailableDates()).thenReturn(Arrays.asList("2024-07-03", "2024-07-04"));

        // when: /api/concert/v1/reservations/dates 엔드포인트에 GET 요청을 보냄
        mockMvc.perform(get("/api/concert/v1/reservations/dates"))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 날짜가 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("2024-07-03"))
                .andExpect(jsonPath("$[1]").value("2024-07-04"));
    }

    /**
     * 예약 가능 좌석 조회 API 테스트
     */
    @Test
    void testGetAvailableSeats() throws Exception {
        // given: 예약 날짜 요청 생성
        ReservationDateRequest dateRequest = new ReservationDateRequest();
        dateRequest.setDate("2024-07-03");

        // given: 예약 좌석 정보 생성
        ReservationResponse seat1 = new ReservationResponse();
        seat1.setSeatNumber(1);
        seat1.setAvailable(true);

        ReservationResponse seat2 = new ReservationResponse();
        seat2.setSeatNumber(2);
        seat2.setAvailable(false);

        // given: ConcertFacade의 getAvailableSeats 메서드가 예약 좌석 리스트를 반환하도록 설정
        when(concertFacade.getAvailableSeats(any(ReservationDateRequest.class)))
                .thenReturn(Arrays.asList(seat1, seat2));

        // when: /api/concert/v1/reservations/seats 엔드포인트에 GET 요청을 보냄
        mockMvc.perform(get("/api/concert/v1/reservations/seats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dateRequest)))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 좌석 정보가 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].seatNumber").value(2))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    /**
     * 좌석 예약 요청 API 테스트
     */
    @Test
    void testReserveSeat() throws Exception {
        // given: 좌석 예약 요청 생성
        SeatReservationRequest reservationRequest = new SeatReservationRequest();
        reservationRequest.setDate("2024-07-03");
        reservationRequest.setSeatNumber(1);
        reservationRequest.setToken("mockToken");

        // given: 좌석 예약 응답 생성
        SeatReservationResponse reservationResponse = new SeatReservationResponse();
        reservationResponse.setReservationId("123e4567-e89b-12d3-a456-426614174001");
        reservationResponse.setExpiresAt(System.currentTimeMillis() + 3600000);

        // given: ConcertFacade의 reserveSeat 메서드가 예약 응답을 반환하도록 설정
        when(concertFacade.reserveSeat(any(SeatReservationRequest.class))).thenReturn(reservationResponse);

        // when: /api/concert/v1/reservations 엔드포인트에 POST 요청을 보냄
        mockMvc.perform(post("/api/concert/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationRequest)))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 예약 ID가 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value("123e4567-e89b-12d3-a456-426614174001"));
    }

    /**
     * 잔액 충전 API 테스트
     */
    @Test
    void testChargeBalance() throws Exception {
        // given: 잔액 충전 요청 생성
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setUserId("123e4567-e89b-12d3-a456-426614174000");
        balanceRequest.setAmount(100);

        // given: 잔액 충전 응답 생성
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setNewBalance(200);

        // given: ConcertFacade의 chargeBalance 메서드가 잔액 응답을 반환하도록 설정
        when(concertFacade.chargeBalance(any(BalanceRequest.class))).thenReturn(balanceResponse);

        // when: /api/concert/v1/balance 엔드포인트에 POST 요청을 보냄
        mockMvc.perform(post("/api/concert/v1/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(balanceRequest)))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 새 잔액이 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalance").value(200));
    }

    /**
     * 잔액 조회 API 테스트
     */
    @Test
    void testGetBalance() throws Exception {
        // given: 잔액 조회 응답 생성
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setCurrentBalance(200);

        // given: ConcertFacade의 getBalance 메서드가 잔액 응답을 반환하도록 설정
        when(concertFacade.getBalance(any(String.class))).thenReturn(balanceResponse);

        // when: /api/concert/v1/balance 엔드포인트에 GET 요청을 보냄
        mockMvc.perform(get("/api/concert/v1/balance")
                .param("userId", "123e4567-e89b-12d3-a456-426614174000"))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 현재 잔액이 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBalance").value(200));
    }

    /**
     * 결제 요청 API 테스트
     */
    @Test
    void testMakePayment() throws Exception {
        // given: 결제 요청 생성
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId("123e4567-e89b-12d3-a456-426614174001");
        paymentRequest.setAmount(100);
        paymentRequest.setToken("mockToken");

        // given: 결제 응답 생성
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId("123e4567-e89b-12d3-a456-426614174002");
        paymentResponse.setStatus("success");

        // given: ConcertFacade의 makePayment 메서드가 결제 응답을 반환하도록 설정
        when(concertFacade.makePayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // when: /api/concert/v1/payments 엔드포인트에 POST 요청을 보냄
        mockMvc.perform(post("/api/concert/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 결제 ID와 상태가 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("123e4567-e89b-12d3-a456-426614174002"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    /**
     * 대기열 확인 API 테스트
     */
    @Test
    void testCheckQueue() throws Exception {
        // given: 대기열 요청 생성
        QueueRequest queueRequest = new QueueRequest("mockToken");

        // given: 대기열 응답 생성
        QueueResponse queueResponse = new QueueResponse();
        queueResponse.setQueuePosition(1);
        queueResponse.setRemainingTime(300);

        // given: ConcertFacade의 checkQueue 메서드가 대기열 응답을 반환하도록 설정
        when(concertFacade.checkQueue(any(QueueRequest.class))).thenReturn(queueResponse);

        // when: /api/concert/v1/queue 엔드포인트에 GET 요청을 보냄
        mockMvc.perform(get("/api/concert/v1/queue")
                .param("token", "mockToken"))
                // then: 응답 상태가 200이고, 응답 바디에 예상된 대기열 위치와 남은 시간이 포함되어 있는지 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(300));
    }
}