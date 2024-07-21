package io.hhplus.concert.reservation.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByUserId(String userId);
    List<TokenEntity> findByStatusOrderByCreatedAt(TokenStatus status);
    List<TokenEntity> findByExpiresAtBefore(LocalDateTime dateTime);
    void deleteByUserId(String userId);
    List<TokenEntity> findTop10ByStatusOrderByCreatedAt(TokenStatus status);
}
