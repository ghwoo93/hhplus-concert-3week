package io.hhplus.concert.reservation.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.exception.ConcertNotFoundException;
import io.hhplus.concert.reservation.application.exception.GlobalExceptionHandler;
import io.hhplus.concert.reservation.application.exception.SeatAlreadyReservedException;
import io.hhplus.concert.reservation.application.exception.TokenInvalidStatusException;
import io.hhplus.concert.reservation.application.facade.ConcertFacade;
import io.hhplus.concert.reservation.presentation.controller.ReservationController;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;

public class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConcertFacade concertFacade;

    @InjectMocks
    private ReservationController reservationController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testGetAvailableConcertDates() throws Exception {
        List<ConcertDTO> concerts = Arrays.asList(
            new ConcertDTO("1", "Concert A", "2023-07-14"),
            new ConcertDTO("2", "Concert B", "2023-08-20")
        );
        when(concertFacade.getAllConcerts()).thenReturn(concerts);
        

        mockMvc.perform(get("/api/v1/reservations/dates")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].concertId").value("1"))
                .andExpect(jsonPath("$[0].concertName").value("Concert A"))
                .andExpect(jsonPath("$[0].date").value("2023-07-14"))
                .andExpect(jsonPath("$[1].concertId").value("2"))
                .andExpect(jsonPath("$[1].concertName").value("Concert B"))
                .andExpect(jsonPath("$[1].date").value("2023-08-20"));

        verify(concertFacade).getAllConcerts();
    }

    @Test
    public void testGetAvailableSeats() throws Exception {
        String concertId = "1";
        SeatDTO seat1 = new SeatDTO(1, true);
        SeatDTO seat2 = new SeatDTO(2, false);

        when(concertFacade.getSeatsByConcertId(concertId)).thenReturn(Arrays.asList(seat1, seat2));

        mockMvc.perform(get("/api/v1/reservations/{concertId}/seats", concertId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].seatNumber").value(2))
                .andExpect(jsonPath("$[1].available").value(false));

        verify(concertFacade).getSeatsByConcertId(concertId);
    }

    @Test
    public void testReserveSeat() throws Exception {
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("validToken");
        request.setConcertId("concert1");
        request.setSeatNumber(1);
        request.setUserId("user1");

        ReservationResponse response = new ReservationResponse("reservation1", LocalDateTime.now().plusMinutes(5));

        when(concertFacade.reserveSeat(any(SeatReservationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value("reservation1"))
                .andExpect(jsonPath("$.expiresAt").exists());

        verify(concertFacade).reserveSeat(any(SeatReservationRequest.class));
    }

    @Test
    public void testReserveSeat_InvalidToken() throws Exception {
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("invalidToken");
        request.setConcertId("concert1");
        request.setSeatNumber(1);
        request.setUserId("user1");
    
        when(concertFacade.reserveSeat(any(SeatReservationRequest.class))).thenThrow(new TokenInvalidStatusException());
    
        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());  // 404 상태 코드를 예상
    }

    @Test
    public void testReserveSeat_SeatAlreadyReserved() throws Exception {
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("validToken");
        request.setConcertId("concert1");
        request.setSeatNumber(1);
        request.setUserId("user1");

        when(concertFacade.reserveSeat(any(SeatReservationRequest.class))).thenThrow(new SeatAlreadyReservedException());

        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetAvailableConcertDates_NoConcerts() throws Exception {
        when(concertFacade.getAllConcerts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reservations/dates")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testGetAvailableSeats_ConcertNotFound() throws Exception {
        String nonExistentConcertId = "nonexistent";
        when(concertFacade.getSeatsByConcertId(nonExistentConcertId)).thenThrow(new ConcertNotFoundException());

        mockMvc.perform(get("/api/v1/reservations/{concertId}/seats", nonExistentConcertId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
