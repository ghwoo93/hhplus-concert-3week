package io.hhplus.concert.reservation.domain.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;

@Component
public class TokenStatusScheduler {
    private static final Logger logger = LoggerFactory.getLogger(TokenStatusScheduler.class);

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

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void expireTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<TokenEntity> expiredTokens = tokenRepository.findByExpiresAtBefore(now);
        for (TokenEntity token : expiredTokens) {
            token.setStatus(TokenStatus.EXPIRED);
            tokenRepository.save(token);
            logger.info("Token expired for user: {}", token.getUserId());
        }
    }

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    @Transactional
    public void activateTokens() {
        List<TokenEntity> waitingTokens = tokenRepository.findTop10ByStatusOrderByCreatedAt(TokenStatus.WAITING);
        for (int i = 0; i < waitingTokens.size(); i++) {
            TokenEntity token = waitingTokens.get(i);
            token.setStatus(TokenStatus.ACTIVE);
            tokenRepository.save(token);
            logger.info("Token activated for user: {}", token.getUserId());
        }
    }
}
