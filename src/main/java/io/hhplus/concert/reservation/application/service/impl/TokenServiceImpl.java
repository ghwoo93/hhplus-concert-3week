package io.hhplus.concert.reservation.application.service.impl;



import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.TokenService;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.infrastructure.mapper.TokenMapper;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public Token createToken(String userId) {
        Token newToken = new Token();
        newToken.setUserId(userId);
        newToken.setToken(generateUniqueToken());
        newToken.setCreatedAt(LocalDateTime.now());
        newToken.setExpiresAt(LocalDateTime.now().plusHours(24)); // 24시간 후 만료

        return TokenMapper.toModel(tokenRepository.save(TokenMapper.toEntity(newToken)));
    }

    @Override
    @Transactional(readOnly = true)
    public Token getToken(String token) {
        return tokenRepository.findByToken(token)
                .map(TokenMapper::toModel)
                .orElseThrow(() -> new TokenNotFoundException());
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
        Token existingToken = getToken(token);
        existingToken.setExpiresAt(LocalDateTime.now());
        tokenRepository.save(TokenMapper.toEntity(existingToken));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenValid(String token) {
        try {
            Token existingToken = getToken(token);
            return !existingToken.isExpired();
        } catch (TokenNotFoundException e) {
            return false;
        }
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
}