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

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.application.exception.InsufficientBalanceException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.controller.PaymentController;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;

public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConcertFacade concertFacade;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    public void testRechargeBalance_Success() throws Exception {
        BalanceResponse response = new BalanceResponse(1000, 1000);
        when(concertFacade.rechargeBalance(anyString(), any(BigDecimal.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"user1\",\"amount\":1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalance").value(1000))
                .andExpect(jsonPath("$.currentBalance").value(1000));
    }

    @Test
    public void testRechargeBalance_Failure() throws Exception {
        when(concertFacade.rechargeBalance(anyString(), any(BigDecimal.class)))
                .thenThrow(new IllegalArgumentException("Invalid amount"));

        mockMvc.perform(post("/api/v1/payments/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"user1\",\"amount\":-1000}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBalance_Success() throws Exception {
        BalanceResponse response = new BalanceResponse(1000, 1000);
        when(concertFacade.getBalance(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments/balance/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalance").value(1000))
                .andExpect(jsonPath("$.currentBalance").value(1000));
    }

    @Test
    public void testProcessPayment_Success() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO("payment1", "COMPLETED");
        when(concertFacade.processPayment(anyString(), any(BigDecimal.class), anyString())).thenReturn(paymentDTO);

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reservationId\":\"res1\",\"amount\":1000,\"token\":\"token1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("payment1"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    public void testProcessPayment_InsufficientBalance() throws Exception {
        when(concertFacade.processPayment(anyString(), any(BigDecimal.class), anyString()))
                .thenThrow(new InsufficientBalanceException());

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reservationId\":\"res1\",\"amount\":10000,\"token\":\"token1\"}"))
                .andExpect(status().isForbidden());
    }
}
