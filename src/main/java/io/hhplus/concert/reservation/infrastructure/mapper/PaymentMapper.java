package io.hhplus.concert.reservation.infrastructure.mapper;

import io.hhplus.concert.reservation.application.dto.PaymentDTO;
import io.hhplus.concert.reservation.domain.model.Payment;
import io.hhplus.concert.reservation.infrastructure.entity.PaymentEntity;

public class PaymentMapper {

    public static Payment entityToDomain(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Payment(
            entity.getId(),
            entity.getUserId(),
            entity.getReservationId(),
            entity.getAmount(),
            entity.getPaymentStatus(),
            entity.getPaidAt()
        );
    }

    public static PaymentEntity domainToEntity(Payment domain) {
        if (domain == null) {
            return null;
        }
        PaymentEntity entity = new PaymentEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setReservationId(domain.getReservationId());
        entity.setAmount(domain.getAmount());
        entity.setPaymentStatus(domain.getPaymentStatus());
        entity.setPaidAt(domain.getPaidAt());
        return entity;
    }

    public static PaymentDTO domainToDto(Payment domain) {
        if (domain == null) {
            return null;
        }
        return new PaymentDTO(domain.getId().toString(), domain.getPaymentStatus());
    }
}
