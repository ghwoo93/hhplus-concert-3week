package io.hhplus.concert.reservation.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.TokenNotFoundException;
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
        Token newToken = Token.createNewToken(userId);
        return saveToken(newToken);
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
        existingToken.invalidate();
        saveToken(existingToken);
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

    private Token saveToken(Token token) {
        return TokenMapper.toModel(tokenRepository.save(TokenMapper.toEntity(token)));
    }
}