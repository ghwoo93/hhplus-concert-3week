package io.hhplus.concert.reservation.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.service.interfaces.ConcertFacade;
import io.hhplus.concert.reservation.presentation.controller.UserController;
import io.hhplus.concert.reservation.presentation.request.QueueStatusRequest;
import io.hhplus.concert.reservation.presentation.request.UserTokenRequest;

@WebMvcTest(UserController.class)
public class UserTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacade concertFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
    void checkQueueStatus_ShouldReturnQueueStatus() throws Exception {
        QueueStatusRequest request = new QueueStatusRequest();
        request.setToken("token123");

        QueueDTO queueDTO = new QueueDTO(1, "ACTIVE", 3600);

        when(concertFacade.checkQueueStatus(anyString())).thenReturn(queueDTO);

        mockMvc.perform(get("/api/v1/queue/status")
                .contentType("application/json")
                .content("{\"token\":\"token123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(3600));
    }
}
