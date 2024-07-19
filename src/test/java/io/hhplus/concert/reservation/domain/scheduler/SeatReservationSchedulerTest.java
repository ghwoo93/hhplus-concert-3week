package io.hhplus.concert.reservation.domain.scheduler;

import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SeatReservationSchedulerTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatReservationScheduler seatReservationScheduler;

    @Captor
    private ArgumentCaptor<LocalDateTime> timeCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReleaseExpiredReservations() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        SeatEntity expiredSeat1 = new SeatEntity();
        expiredSeat1.setReserved(true);
        expiredSeat1.setReservedUntil(now.minusMinutes(1));

        SeatEntity expiredSeat2 = new SeatEntity();
        expiredSeat2.setReserved(true);
        expiredSeat2.setReservedUntil(now.minusMinutes(2));

        List<SeatEntity> expiredSeats = Arrays.asList(expiredSeat1, expiredSeat2);
        when(seatRepository.findByReservedUntilLessThan(any(LocalDateTime.class))).thenReturn(expiredSeats);

        // When
        seatReservationScheduler.releaseExpiredReservations();

        // Then
        verify(seatRepository).findByReservedUntilLessThan(timeCaptor.capture());
        LocalDateTime capturedTime = timeCaptor.getValue();
        assertNotNull(capturedTime);
        
        verify(seatRepository, times(2)).save(any(SeatEntity.class));
        
        for (SeatEntity seat : expiredSeats) {
            assertFalse(seat.isReserved());
            assertNull(seat.getReservedBy());
            assertNull(seat.getReservedUntil());
        }
    }
}
