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

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.exception.UserAlreadyInQueueException;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.controller.UserController;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConcertFacade concertFacade;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testIssueToken_Success() throws Exception {
        TokenDTO tokenDTO = new TokenDTO("token1", "ACTIVE", 1, 3600L);
        when(concertFacade.issueToken(anyString())).thenReturn(tokenDTO);

        mockMvc.perform(post("/api/v1/users/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token1"))
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.remainingTime").value(3600L));
    }

    @Test
    public void testIssueToken_UserNotFound() throws Exception {
        when(concertFacade.issueToken(anyString())).thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/api/v1/users/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"nonexistent\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testIssueToken_UserAlreadyInQueue() throws Exception {
        when(concertFacade.issueToken(anyString())).thenThrow(new UserAlreadyInQueueException("User already in queue"));

        mockMvc.perform(post("/api/v1/users/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"user1\"}"))
                .andExpect(status().isConflict());
    }
}
