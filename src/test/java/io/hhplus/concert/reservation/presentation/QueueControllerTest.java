package io.hhplus.concert.reservation.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.concert.reservation.application.dto.QueueDTO;
import io.hhplus.concert.reservation.application.exception.TokenExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.exception.UserAlreadyInQueueException;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.config.TestSecurityConfig;
import io.hhplus.concert.reservation.presentation.controller.QueueController;
import io.hhplus.concert.reservation.presentation.request.QueueRequest;
import io.hhplus.concert.reservation.presentation.request.QueueStatusRequest;


@WebMvcTest(QueueController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacade concertFacade;

    @Autowired
    private ObjectMapper objectMapper;

    private QueueDTO queueDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new QueueController(concertFacade)).build();

        queueDTO = new QueueDTO();
        queueDTO.setQueuePosition(1);
        queueDTO.setRemainingTime(600L);
    }

    @Test
    public void testCreateQueue_Success() throws Exception {
        QueueDTO queueDTO = new QueueDTO(1, "WAITING", 3600);
        when(concertFacade.createQueue(anyString())).thenReturn(queueDTO);

        QueueRequest request = new QueueRequest("user123");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/queue/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateQueue_UserNotFound() throws Exception {
        when(concertFacade.createQueue(anyString())).thenThrow(new UserNotFoundException());

        QueueRequest request = new QueueRequest("user123");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/queue/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateQueue_UserAlreadyInQueue() throws Exception {
        String userId = "user123";
        when(concertFacade.createQueue(anyString())).thenThrow(new UserAlreadyInQueueException(userId));

        QueueRequest request = new QueueRequest(userId);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/queue/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict());
    }
    
    @Test
    public void testCreateQueue_InternalServerError() throws Exception {
        when(concertFacade.createQueue(anyString())).thenThrow(new RuntimeException());

        QueueRequest request = new QueueRequest("user123");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/queue/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void createQueue_ShouldReturnQueue() throws Exception {
        String userId = "user1";
        QueueDTO expectedQueueDTO = new QueueDTO(1, "WAITING", 3600);
    
        when(concertFacade.createQueue(eq(userId))).thenReturn(expectedQueueDTO);
    
        mockMvc.perform(post("/api/v1/queue/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"" + userId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.queuePosition").value(expectedQueueDTO.getQueuePosition()))
                .andExpect(jsonPath("$.status").value(expectedQueueDTO.getStatus()))
                .andExpect(jsonPath("$.remainingTime").value(expectedQueueDTO.getRemainingTime()))
                .andDo(print());
    
        verify(concertFacade).createQueue(eq(userId));
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

        @Test
    void checkQueueStatus_shouldReturnQueueStatus() throws Exception {
        Mockito.when(concertFacade.checkQueueStatus(anyString())).thenReturn(queueDTO);

        QueueStatusRequest request = new QueueStatusRequest();
        request.setToken("valid-token");

        mockMvc.perform(get("/api/v1/queue/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"valid-token\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.queuePosition").value(queueDTO.getQueuePosition()))
                .andExpect(jsonPath("$.remainingTime").value(queueDTO.getRemainingTime()));
    }

    @Test
    void checkQueueStatus_shouldReturnNotFound_whenTokenNotFound() throws Exception {
        Mockito.when(concertFacade.checkQueueStatus(anyString())).thenThrow(new TokenNotFoundException());

        mockMvc.perform(get("/api/v1/queue/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"invalid-token\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkQueueStatus_shouldReturnGone_whenTokenExpired() throws Exception {
        Mockito.when(concertFacade.checkQueueStatus(anyString())).thenThrow(new TokenExpiredException());

        mockMvc.perform(get("/api/v1/queue/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"expired-token\"}"))
                .andExpect(status().isGone());
    }
}
