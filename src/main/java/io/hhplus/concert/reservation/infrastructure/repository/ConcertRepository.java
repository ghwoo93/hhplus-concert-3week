package io.hhplus.concert.reservation.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hhplus.concert.reservation.infrastructure.entity.ConcertEntity;

@Repository
public interface ConcertRepository extends JpaRepository<ConcertEntity, String> {
}