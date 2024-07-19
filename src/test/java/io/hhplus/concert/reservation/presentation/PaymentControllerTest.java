package io.hhplus.concert.reservation.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.exception.GlobalExceptionHandler;
import io.hhplus.concert.reservation.application.exception.InsufficientBalanceException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.controller.PaymentController;
import io.hhplus.concert.reservation.presentation.request.BalanceRequest;
import io.hhplus.concert.reservation.presentation.request.PaymentRequest;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;

public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConcertFacade concertFacade;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRechargeBalance_Success() throws Exception {
        BalanceResponse response = new BalanceResponse(1000, 1000);
        when(concertFacade.rechargeBalance(anyString(), any(BigDecimal.class))).thenReturn(response);

        BalanceRequest request = new BalanceRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("1000"));

        mockMvc.perform(post("/api/v1/payments/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalance").value(1000))
                .andExpect(jsonPath("$.currentBalance").value(1000));

        verify(concertFacade).rechargeBalance("user1", new BigDecimal("1000"));
    }

    @Test
    public void testGetBalance_Success() throws Exception {
        BalanceResponse response = new BalanceResponse(1000, 1000);
        when(concertFacade.getBalance(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments/balance/{userId}", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalance").value(1000))
                .andExpect(jsonPath("$.currentBalance").value(1000));

        verify(concertFacade).getBalance("user1");
    }

    @Test
    public void testProcessPayment_Success() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO("payment1", "COMPLETED");
        when(concertFacade.processPayment(anyString(), any(BigDecimal.class), anyString())).thenReturn(paymentDTO);

        PaymentRequest request = new PaymentRequest();
        request.setReservationId("res1");
        request.setAmount(new BigDecimal("1000"));
        request.setToken("token1");

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("payment1"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(concertFacade).processPayment("res1", new BigDecimal("1000"), "token1");
    }

    @Test
    public void testProcessPayment_InsufficientBalance() throws Exception {
        when(concertFacade.processPayment(anyString(), any(BigDecimal.class), anyString()))
                .thenThrow(new InsufficientBalanceException());

        PaymentRequest request = new PaymentRequest();
        request.setReservationId("res1");
        request.setAmount(new BigDecimal("10000"));
        request.setToken("token1");

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Insufficient balance."));

        verify(concertFacade).processPayment("res1", new BigDecimal("10000"), "token1");
    }
}
