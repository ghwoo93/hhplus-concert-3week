package io.hhplus.concert.reservation.domain.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.domain.enums.SeatStatus;
import io.hhplus.concert.reservation.infrastructure.entity.SeatEntity;
import io.hhplus.concert.reservation.infrastructure.repository.SeatRepository;

@Component
public class SeatReservationScheduler {
    private final SeatRepository seatRepository;

    @Autowired
    public SeatReservationScheduler(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        List<SeatEntity> expiredSeats = seatRepository.findByIdStatusAndReservedUntilLessThan(
            SeatStatus.RESERVED, now);

        for (SeatEntity seat : expiredSeats) {
            int updatedRows = seatRepository.updateSeatStatus(
                seat.getId().getConcertId(),
                seat.getId().getSeatNumber(),
                SeatStatus.RESERVED,
                SeatStatus.AVAILABLE,
                null,
                null
            );

            if (updatedRows == 0) {
                // 업데이트에 실패한 경우 (이미 다른 프로세스에 의해 처리됐을 수 있음)
                // 로깅 또는 추가 처리를 수행할 수 있습니다.
            }
        }
    }
}
