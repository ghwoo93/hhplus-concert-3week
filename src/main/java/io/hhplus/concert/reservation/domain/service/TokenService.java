package io.hhplus.concert.reservation.domain.service;

import io.hhplus.concert.reservation.domain.model.Token;

public interface TokenService {
    Token getTokenStatus(String userId);
    void updateTokenPosition(Token token);
    Token createNewToken(String userId);
    Token getOrCreateTokenForUser(String userId);
    void expireToken(Token token);
    boolean isTokenValid(String userId);
}