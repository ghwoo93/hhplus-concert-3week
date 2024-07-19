package io.hhplus.concert.reservation.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hhplus.concert.reservation.domain.enums.TokenStatus;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByUserId(String userId);
    List<TokenEntity> findByStatusOrderByCreatedAt(TokenStatus status);
    void deleteByUserId(String userId);
}
