package io.hhplus.concert.reservation.domain.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.concert.reservation.domain.enums.SeatStatus;
import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
public class SeatReservationSchedulerTest {

    @Mock
    private SeatRepository seatRepository;

    private SeatReservationScheduler seatReservationScheduler;

    @Captor
    private ArgumentCaptor<SeatStatus> statusCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> timeCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seatReservationScheduler = new SeatReservationScheduler(seatRepository);
    }

    @Test
    void testReleaseExpiredReservations() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        SeatEntity.SeatId id1 = new SeatEntity.SeatId("concert1", 1, SeatStatus.RESERVED);
        SeatEntity.SeatId id2 = new SeatEntity.SeatId("concert2", 2, SeatStatus.RESERVED);

        SeatEntity expiredSeat1 = new SeatEntity(id1, "user1", now.minusMinutes(1));
        SeatEntity expiredSeat2 = new SeatEntity(id2, "user2", now.minusMinutes(2));

        List<SeatEntity> expiredSeats = Arrays.asList(expiredSeat1, expiredSeat2);
        
        // When
        when(seatRepository.findByIdStatusAndReservedUntilLessThan(any(SeatStatus.class), any(LocalDateTime.class)))
            .thenReturn(expiredSeats);
        when(seatRepository.updateSeatStatus(anyString(), anyInt(), any(SeatStatus.class), any(SeatStatus.class), isNull(), isNull()))
            .thenReturn(1);

        seatReservationScheduler.releaseExpiredReservations();

        // Then
        verify(seatRepository).findByIdStatusAndReservedUntilLessThan(statusCaptor.capture(), timeCaptor.capture());
        assertEquals(SeatStatus.RESERVED, statusCaptor.getValue());
        LocalDateTime capturedTime = timeCaptor.getValue();
        assertNotNull(capturedTime);
        assertTrue(capturedTime.isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(capturedTime.isAfter(LocalDateTime.now().minusSeconds(1)));
        
        verify(seatRepository).updateSeatStatus(
            eq("concert1"), eq(1), eq(SeatStatus.RESERVED), eq(SeatStatus.AVAILABLE), isNull(), isNull());
        verify(seatRepository).updateSeatStatus(
            eq("concert2"), eq(2), eq(SeatStatus.RESERVED), eq(SeatStatus.AVAILABLE), isNull(), isNull());
    }
}
