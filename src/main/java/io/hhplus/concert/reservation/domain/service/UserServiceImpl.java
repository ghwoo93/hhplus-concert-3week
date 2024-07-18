package io.hhplus.concert.reservation.domain.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
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
        User user = findUserById(userId);
        user.rechargeBalance(amount);
        User updatedUser = saveUser(user);
        return createBalanceResponse(updatedUser.getBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String userId) {
        User user = findUserById(userId);
        return createBalanceResponse(user.getBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return findUserById(userId);
    }

    @Override
    @Transactional
    public BalanceResponse deductBalance(String userId, BigDecimal amount) {
        User user = findUserById(userId);
        user.deductBalance(amount);
        User updatedUser = saveUser(user);
        return createBalanceResponse(updatedUser.getBalance());
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .map(UserEntity::toUser)
                .orElseThrow(UserNotFoundException::new);
    }

    private User saveUser(User user) {
        UserEntity savedEntity = userRepository.save(new UserEntity(user));
        return savedEntity.toUser();
    }

    private BalanceResponse createBalanceResponse(BigDecimal balance) {
        int balanceValue = balance.intValue();
        return new BalanceResponse(balanceValue, balanceValue);
    }
}
