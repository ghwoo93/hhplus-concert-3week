package io.hhplus.concert.reservation.presentation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hhplus.concert.reservation.application.dto.ConcertDTO;
import io.hhplus.concert.reservation.application.dto.SeatDTO;
import io.hhplus.concert.reservation.application.service.facade.ConcertFacadeImpl;
import io.hhplus.concert.reservation.infrastructure.mapper.ResponseMapper;
import io.hhplus.concert.reservation.presentation.response.ConcertDateResponse;
import io.hhplus.concert.reservation.presentation.response.SeatResponse;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    
    private final ConcertFacadeImpl concertFacade;

    @Autowired
    public ReservationController(ConcertFacadeImpl concertFacade) {
        this.concertFacade = concertFacade;
    }

    // @PostMapping
    // public ResponseEntity<ReservationResponse> reserveSeat(@RequestBody ReservationRequest reservationRequest) {
    //     ReservationResponse reservationResponse = concertFacade.reserveSeat(reservationRequest.getConcertId(), reservationRequest.getSeatNumber(), reservationRequest.getToken());
    //     return ResponseEntity.ok(reservationResponse);
    // }

    @GetMapping("/dates")
    public ResponseEntity<List<ConcertDateResponse>> getAvailableConcertDates() {
        List<ConcertDTO> concertDates = concertFacade.getAllConcerts();
        List<ConcertDateResponse> response = ResponseMapper.toConcertDateResponseList(concertDates);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertId}/seats")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable String concertId) {
        List<SeatDTO> seats = concertFacade.getSeatsByConcertId(concertId);
        List<SeatResponse> response = ResponseMapper.toSeatResponseList(seats);
        return ResponseEntity.ok(response);
    }
}
