package io.hhplus.concert.reservation.domain.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.TokenExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenInvalidStatusException;
import io.hhplus.concert.reservation.application.facade.ConcertFacadeImpl;
import io.hhplus.concert.reservation.config.JwtConfig;
import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;
import io.hhplus.concert.reservation.infrastructure.mapper.TokenMapper;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenServiceImpl implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(ConcertFacadeImpl.class);
    private final JwtConfig jwtConfig;

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository, JwtConfig jwtConfig) {
        this.tokenRepository = tokenRepository;
        this.jwtConfig = jwtConfig;
    }

    @Override
    @Transactional(readOnly = true)
    public Token getTokenStatus(String userId) {
        Token token = findTokenByUserId(userId);
        if (token.isExpired()) {
            expireToken(token);
            throw new TokenExpiredException();
        }
        if (token.getStatus() == TokenStatus.WAITING) {
            updateTokenPosition(token);
        }
        return token;
    }

    @Override
    @Transactional
    public void updateTokenPosition(Token token) {
        List<TokenEntity> waitingTokens = tokenRepository.findByStatusOrderByCreatedAt(TokenStatus.WAITING);
        for (int i = 0; i < waitingTokens.size(); i++) {
            TokenEntity tokenEntity = waitingTokens.get(i);
            tokenEntity.setQueuePosition(i + 1);
            tokenRepository.save(tokenEntity);
        }
    }

    @Override
    public boolean isTokenValid(String userId) {
        return tokenRepository.findByUserId(userId)
                            .map(TokenMapper::toModel)
                            .map(this::isTokenActive)
                            .orElse(false);
    }

    private boolean isTokenActive(Token token) {
        boolean isActive = token.getStatus() == TokenStatus.ACTIVE;
        boolean isExpired = token.isExpired();
        logger.debug("isTokenActive isActive : {}, isExpired: {}", isActive, isExpired);
        logger.debug("isTokenActive Token : {}", token.toString());
        return token.getStatus() == TokenStatus.ACTIVE && !token.isExpired();
    }

    @Override
    @Transactional
    public Token getOrCreateTokenForUser(String userId) {
        return tokenRepository.findByUserId(userId)
        .map(TokenMapper::toModel)
        .orElseGet(() -> createNewToken(userId));
    }
    
    private Token createNewToken(String userId) {
        Token newToken = new Token(userId);
        long tokenCount = tokenRepository.count();
        if (tokenCount < 11) {
            newToken.setStatus(TokenStatus.ACTIVE);
        } else {
            newToken.setStatus(TokenStatus.WAITING);
            newToken.updateQueuePosition(calculateQueuePosition());
        }
        newToken.setId(generateJwtToken(userId));
        return saveToken(newToken);
    }

    private String generateJwtToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .compact();
    }

    @Override
    @Transactional
    public void expireToken(Token token) {
        token.expire();
        saveToken(token);
    }

    private Token findTokenByUserId(String userId) {
        return tokenRepository.findByUserId(userId)
                .map(TokenMapper::toModel)
                .orElseThrow(() -> new TokenInvalidStatusException("Token not found for user: " + userId));
    }

    private int calculateQueuePosition() {
        return (int) tokenRepository.count() + 1;
    }

    private Token saveToken(Token token) {
        TokenEntity entityToSave = TokenMapper.toEntity(token);
        TokenEntity savedEntity = tokenRepository.save(entityToSave);
        logger.debug("Token saved: {}", savedEntity.toString());
        return TokenMapper.toModel(savedEntity);
    }
}