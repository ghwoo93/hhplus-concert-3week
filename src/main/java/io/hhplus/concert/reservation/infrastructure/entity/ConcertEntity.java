package io.hhplus.concert.reservation.infrastructure.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "concerts")
public class ConcertEntity {
    @Id
    private String id;
    private String concertName;
    private LocalDate date;
}
