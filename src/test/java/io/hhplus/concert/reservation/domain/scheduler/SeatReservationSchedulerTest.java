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

import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
public class SeatReservationSchedulerTest {

    @Mock
    private SeatRepository seatRepository;

    private SeatReservationScheduler seatReservationScheduler;

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
        SeatEntity expiredSeat1 = new SeatEntity();
        expiredSeat1.setReserved(true);
        expiredSeat1.setReservedBy("user1");
        expiredSeat1.setReservedUntil(now.minusMinutes(1));

        SeatEntity expiredSeat2 = new SeatEntity();
        expiredSeat2.setReserved(true);
        expiredSeat2.setReservedBy("user2");
        expiredSeat2.setReservedUntil(now.minusMinutes(2));

        List<SeatEntity> expiredSeats = Arrays.asList(expiredSeat1, expiredSeat2);
        
        // When
        when(seatRepository.findByReservedUntilLessThan(any(LocalDateTime.class))).thenReturn(expiredSeats);

        seatReservationScheduler.releaseExpiredReservations();

        // Then
        verify(seatRepository).findByReservedUntilLessThan(timeCaptor.capture());
        LocalDateTime capturedTime = timeCaptor.getValue();
        assertNotNull(capturedTime);
        assertTrue(capturedTime.isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(capturedTime.isAfter(LocalDateTime.now().minusSeconds(1)));
        
        verify(seatRepository, times(2)).save(any(SeatEntity.class));
        
        for (SeatEntity seat : expiredSeats) {
            assertFalse(seat.isReserved());
            assertNull(seat.getReservedBy());
            assertNull(seat.getReservedUntil());
        }
    }
}
