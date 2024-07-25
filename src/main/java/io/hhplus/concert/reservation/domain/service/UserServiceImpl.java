package io.hhplus.concert.reservation.domain.service;

import java.math.BigDecimal;

import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, Long> redisTemplate;

    public UserServiceImpl(UserRepository userRepository, RedisTemplate<String, Long> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public BalanceResponse rechargeBalance(String userId, BigDecimal amount) {
        User user = findUserById(userId);
        String balanceKey = "user:balance:" + userId;
        
        Long newBalance = redisTemplate.opsForValue().increment(balanceKey, amount.longValue());
        if (newBalance == null) {
            throw new RuntimeException("Failed to update balance in Redis");
        }
        
        user.setBalance(BigDecimal.valueOf(newBalance));
        User updatedUser = saveUser(user);
        return createBalanceResponse(updatedUser.getBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String userId) {
        String balanceKey = "user:balance:" + userId;
        Long balance = redisTemplate.opsForValue().get(balanceKey);
        
        if (balance == null) {
            User user = findUserById(userId);
            balance = user.getBalance().longValue();
            redisTemplate.opsForValue().set(balanceKey, balance);
        }
        
        return createBalanceResponse(BigDecimal.valueOf(balance));
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
        String balanceKey = "user:balance:" + userId;
        
        Long newBalance = redisTemplate.opsForValue().decrement(balanceKey, amount.longValue());
        if (newBalance == null || newBalance < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        user.setBalance(BigDecimal.valueOf(newBalance));
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
