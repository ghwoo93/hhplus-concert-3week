package io.hhplus.concert.reservation.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.TokenExpiredException;
import io.hhplus.concert.reservation.application.exception.TokenInvalidStatusException;
import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;

import io.hhplus.concert.reservation.infrastructure.mapper.TokenMapper;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final ReentrantLock updateLock = new ReentrantLock();

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
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
    @Transactional(readOnly = true)
    public boolean isTokenValid(String userId) {
        try {
            Token token = getTokenStatus(userId);
            return token.getStatus() == TokenStatus.ACTIVE && !token.isExpired();
        } catch (TokenNotFoundException | TokenExpiredException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public Token createNewToken(String userId) {
        Token newToken = new Token(userId);
        newToken.updateQueuePosition(calculateQueuePosition());
        return saveToken(newToken);
    }

    @Override
    @Transactional
    public Token getOrCreateTokenForUser(String userId) {
        return tokenRepository.findByUserId(userId)
                .map(TokenMapper::toModel)
                .orElseGet(() -> createNewToken(userId));
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
                .orElseThrow(() -> new TokenInvalidStatusException());
    }

    // private List<Token> getWaitingTokens() {
    //     return tokenRepository.findByStatusOrderByCreatedAt(TokenStatus.WAITING)
    //             .stream()
    //             .map(TokenMapper::toModel)
    //             .collect(Collectors.toList());
    // }

    // private int calculatePosition(List<Token> waitingTokens, String userId) {
    //     for (int i = 0; i < waitingTokens.size(); i++) {
    //         if (waitingTokens.get(i).getUserId().equals(userId)) {
    //             return i + 1;
    //         }
    //     }
    //     throw new TokenInvalidStatusException();
    // }

    private int calculateQueuePosition() {
        return (int) tokenRepository.count() + 1;
    }

    private Token saveToken(Token token) {
        TokenEntity entityToSave = TokenMapper.toEntity(token);
        TokenEntity savedEntity = tokenRepository.save(entityToSave);
        return TokenMapper.toModel(savedEntity);
    }
}