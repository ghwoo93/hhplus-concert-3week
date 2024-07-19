package io.hhplus.concert.reservation.domain.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;

@Component
public class SeatReservationScheduler {
    private final SeatRepository seatRepository;

    @Autowired
    public SeatReservationScheduler(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatEntity> expiredSeats = seatRepository.findByReservedUntilLessThan(now);
        for (SeatEntity seat : expiredSeats) {
            seat.setReserved(false);
            seat.setReservedBy(null);
            seat.setReservedUntil(null);
            seatRepository.save(seat);
        }
    }
}
