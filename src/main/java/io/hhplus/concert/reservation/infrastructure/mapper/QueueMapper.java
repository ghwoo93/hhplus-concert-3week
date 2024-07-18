package io.hhplus.concert.reservation.infrastructure.mapper;

import io.hhplus.concert.reservation.domain.model.Queue;
import io.hhplus.concert.reservation.infrastructure.entity.QueueEntity;

public class QueueMapper {
    public static Queue toModel(QueueEntity entity) {
        if (entity == null) {
            return null;
        }
        Queue queue = new Queue();
        queue.setId(entity.getId());
        queue.setUserId(entity.getUserId());
        queue.setToken(entity.getToken());
        queue.setQueuePosition(entity.getQueuePosition());
        queue.setStatus(entity.getStatus());
        queue.setCreatedAt(entity.getCreatedAt());
        queue.setExpiresAt(entity.getExpiresAt());
        return queue;
    }

    public static QueueEntity toEntity(Queue model) {
        if (model == null) {
            return null;
        }
        QueueEntity entity = new QueueEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserId());
        entity.setToken(model.getToken());
        entity.setQueuePosition(model.getQueuePosition());
        entity.setStatus(model.getStatus());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setExpiresAt(model.getExpiresAt());
        return entity;
    }
}
