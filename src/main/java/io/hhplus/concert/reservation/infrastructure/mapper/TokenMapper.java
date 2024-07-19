package io.hhplus.concert.reservation.infrastructure.mapper;

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;

public class TokenMapper {

    public static Token toModel(TokenEntity entity) {
        if (entity == null) {
            return null;
        }
        Token token = new Token(entity.getUserId());
        token.setId(entity.getId());
        token.setQueuePosition(entity.getQueuePosition());
        token.setStatus(TokenStatus.valueOf(entity.getStatus().name()));
        token.setCreatedAt(entity.getCreatedAt());
        token.setExpiresAt(entity.getExpiresAt());
        token.setLastUpdatedAt(entity.getLastUpdatedAt());
        return token;
    }

    public static TokenEntity toEntity(Token model) {
        if (model == null) {
            return null;
        }
        TokenEntity entity = new TokenEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserId());
        entity.setQueuePosition(model.getQueuePosition());
        entity.setStatus(TokenStatus.valueOf(model.getStatus().name()));
        entity.setCreatedAt(model.getCreatedAt());
        entity.setExpiresAt(model.getExpiresAt());
        entity.setLastUpdatedAt(model.getLastUpdatedAt());
        return entity;
    }

    public static TokenDTO toDto(Token token) {
        if (token == null) {
            return null;
        }
        return new TokenDTO(
            token.getId(),
            token.getStatus().name(),
            token.getQueuePosition(),
            token.getRemainingTimeInSeconds()
        );
    }
}