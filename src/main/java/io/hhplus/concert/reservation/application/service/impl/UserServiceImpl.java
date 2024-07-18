package io.hhplus.concert.reservation.application.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.UserService;
import io.hhplus.concert.reservation.domain.model.User;
import io.hhplus.concert.reservation.infrastructure.entity.UserEntity;
import io.hhplus.concert.reservation.infrastructure.repository.UserRepository;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BalanceResponse rechargeBalance(String userId, BigDecimal amount) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        User user = userEntity.toUser();
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(new UserEntity(user));
        return new BalanceResponse(user.getBalance().intValue(), user.getBalance().intValue());
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        User user = userEntity.toUser();
        return new BalanceResponse(user.getBalance().intValue(), user.getBalance().intValue());
    }
}
