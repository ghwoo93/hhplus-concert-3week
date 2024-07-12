package io.hhplus.concert.reservation.infrastructure.mapper;

import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;

public class TokenMapper {

    public static Token toModel(TokenEntity entity) {
        if (entity == null) {
            return null;
        }

        Token token = new Token();
        token.setToken(entity.getToken());
        token.setUserId(entity.getUserId());
        token.setCreatedAt(entity.getCreatedAt());
        token.setExpiresAt(entity.getExpiresAt());

        return token;
    }

    public static TokenEntity toEntity(Token model) {
        if (model == null) {
            return null;
        }

        TokenEntity entity = new TokenEntity();
        entity.setToken(model.getToken());
        entity.setUserId(model.getUserId());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setExpiresAt(model.getExpiresAt());

        return entity;
    }
}