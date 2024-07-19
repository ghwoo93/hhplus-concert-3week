package io.hhplus.concert.reservation.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.exception.GlobalExceptionHandler;
import io.hhplus.concert.reservation.application.exception.TokenExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenInvalidStatusException;
import io.hhplus.concert.reservation.application.exception.UserAlreadyInQueueException;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.controller.TokenController;
import io.hhplus.concert.reservation.presentation.request.TokenRequest;

public class TokenControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConcertFacade concertFacade;

    @InjectMocks
    private TokenController tokenController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tokenController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testIssueToken_Success() throws Exception {
        TokenDTO tokenDTO = new TokenDTO("token123", "ACTIVE", 1, 3600L);
        when(concertFacade.issueToken(anyString())).thenReturn(tokenDTO);

        TokenRequest request = new TokenRequest();
        request.setUserId("user123");

        mockMvc.perform(post("/api/v1/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(3600L));
    }

    @Test
    public void testIssueToken_UserNotFound() throws Exception {
        when(concertFacade.issueToken(anyString())).thenThrow(new UserNotFoundException());

        TokenRequest request = new TokenRequest();
        request.setUserId("nonexistent");

        mockMvc.perform(post("/api/v1/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testIssueToken_UserAlreadyInQueue() throws Exception {
        when(concertFacade.issueToken(anyString())).thenThrow(new UserAlreadyInQueueException("User already in queue"));

        TokenRequest request = new TokenRequest();
        request.setUserId("user123");

        mockMvc.perform(post("/api/v1/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCheckTokenStatus_Success() throws Exception {
        TokenDTO tokenDTO = new TokenDTO("token123", "ACTIVE", 1, 3600L);
        when(concertFacade.checkTokenStatus(anyString())).thenReturn(tokenDTO);

        mockMvc.perform(get("/api/v1/tokens/{userId}", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(3600L));
    }

    @Test
    public void testCheckTokenStatus_TokenNotFound() throws Exception {
        when(concertFacade.checkTokenStatus(anyString())).thenThrow(new TokenInvalidStatusException());

        mockMvc.perform(get("/api/v1/tokens/{userId}", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCheckTokenStatus_TokenExpired() throws Exception {
        when(concertFacade.checkTokenStatus(anyString())).thenThrow(new TokenExpiredException());

        mockMvc.perform(get("/api/v1/tokens/{userId}", "expired"))
                .andExpect(status().isGone());
    }
}