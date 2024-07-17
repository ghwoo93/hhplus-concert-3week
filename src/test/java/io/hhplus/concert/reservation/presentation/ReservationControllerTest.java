package io.hhplus.concert.reservation.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.exception.ConcertNotFoundException;
import io.hhplus.concert.reservation.application.exception.InsufficientBalanceException;
import io.hhplus.concert.reservation.application.exception.SeatAlreadyReservedException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.service.facade.ConcertFacadeImpl;
import io.hhplus.concert.reservation.config.TestSecurityConfig;
import io.hhplus.concert.reservation.presentation.controller.ReservationController;
import io.hhplus.concert.reservation.presentation.request.SeatReservationRequest;
import io.hhplus.concert.reservation.presentation.response.ReservationResponse;

@WebMvcTest(ReservationController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacadeImpl concertFacade;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAvailableConcertDates() throws Exception {
        ConcertDTO concert1 = new ConcertDTO("1", "Concert A", "2023-07-14");
        ConcertDTO concert2 = new ConcertDTO("2", "Concert B", "2023-08-20");

        when(concertFacade.getAllConcerts()).thenReturn(Arrays.asList(concert1, concert2));

        mockMvc.perform(get("/api/v1/reservations/dates")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].concertId").value("1"))
                .andExpect(jsonPath("$[0].concertName").value("Concert A"))
                .andExpect(jsonPath("$[0].date").value("2023-07-14"))
                .andExpect(jsonPath("$[1].concertId").value("2"))
                .andExpect(jsonPath("$[1].concertName").value("Concert B"))
                .andExpect(jsonPath("$[1].date").value("2023-08-20"))
                .andDo(print());
    }

    @Test
    public void testGetAvailableSeats() throws Exception {
        String concertId = "1";
        SeatDTO seat1 = new SeatDTO(1, true);
        SeatDTO seat2 = new SeatDTO(2, false);

        when(concertFacade.getSeatsByConcertId(anyString())).thenReturn(Arrays.asList(seat1, seat2));

        mockMvc.perform(get("/api/v1/reservations/" + concertId + "/seats")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].seatNumber").value(2))
                .andExpect(jsonPath("$[1].available").value(false))
                .andDo(print());
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
                .andExpect(jsonPath("$.expiresAt").exists())
                .andDo(print());

        verify(concertFacade).reserveSeat(any(SeatReservationRequest.class));
    }

    @Test
    public void testReserveSeat_InvalidToken() throws Exception {
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("invalidToken");
        request.setConcertId("concert1");
        request.setSeatNumber(1);
        request.setUserId("user1");

        when(concertFacade.reserveSeat(any(SeatReservationRequest.class))).thenThrow(new TokenNotFoundException());

        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
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
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    public void testGetAvailableConcertDates_NoConcerts() throws Exception {
        when(concertFacade.getAllConcerts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reservations/dates")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(print());
    }

    @Test
    public void testGetAvailableSeats_ConcertNotFound() throws Exception {
        String nonExistentConcertId = "nonexistent";
        when(concertFacade.getSeatsByConcertId(nonExistentConcertId)).thenThrow(new ConcertNotFoundException());

        mockMvc.perform(get("/api/v1/reservations/{concertId}/seats", nonExistentConcertId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andDo(print());
    }

    @Test
    public void testReserveSeat_InsufficientBalance() throws Exception {
        SeatReservationRequest request = new SeatReservationRequest();
        request.setToken("validToken");
        request.setConcertId("concert1");
        request.setSeatNumber(1);
        request.setUserId("user1");

        when(concertFacade.reserveSeat(any(SeatReservationRequest.class))).thenThrow(new InsufficientBalanceException());

        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }    
}
