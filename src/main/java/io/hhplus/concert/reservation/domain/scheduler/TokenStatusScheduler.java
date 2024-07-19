package io.hhplus.concert.reservation.domain.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;

@Component
public class TokenStatusScheduler {
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenStatusScheduler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    @Transactional
    public void updateTokenPositions() {
        List<TokenEntity> waitingTokens = tokenRepository.findByStatusOrderByCreatedAt(TokenStatus.WAITING);
        for (int i = 0; i < waitingTokens.size(); i++) {
            TokenEntity token = waitingTokens.get(i);
            token.setQueuePosition(i + 1);
            tokenRepository.save(token);
        }
    }
}
