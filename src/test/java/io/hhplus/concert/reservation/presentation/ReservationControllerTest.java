package io.hhplus.concert.reservation.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.service.facade.ConcertFacadeImpl;
import io.hhplus.concert.reservation.config.TestSecurityConfig;
import io.hhplus.concert.reservation.presentation.controller.ReservationController;

@WebMvcTest(ReservationController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacadeImpl concertFacade;

    @BeforeEach
    void setUp() {
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
}
