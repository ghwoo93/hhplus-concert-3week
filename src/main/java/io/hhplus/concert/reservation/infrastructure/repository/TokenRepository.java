package io.hhplus.concert.reservation.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByToken(String token);
}
