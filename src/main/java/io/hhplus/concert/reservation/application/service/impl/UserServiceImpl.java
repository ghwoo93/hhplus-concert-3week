package io.hhplus.concert.reservation.application.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.application.exception.UserNotFoundException;
import io.hhplus.concert.reservation.application.service.interfaces.UserService;
import io.hhplus.concert.reservation.domain.model.Token;
import io.hhplus.concert.reservation.domain.model.User;
import io.hhplus.concert.reservation.infrastructure.entity.TokenEntity;
import io.hhplus.concert.reservation.infrastructure.entity.UserEntity;
import io.hhplus.concert.reservation.infrastructure.repository.TokenRepository;
import io.hhplus.concert.reservation.infrastructure.repository.UserRepository;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public TokenDTO generateToken(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User user = userEntity.toUser(); // UserEntity를 User 도메인 객체로 변환
        String tokenString = UUID.randomUUID().toString();
        Token token = new Token(tokenString, userId, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(new TokenEntity(token));
        return new TokenDTO(token.getToken(), calculateQueuePosition(user), estimateWaitingTime(user));
    }

    @Override
    public BalanceResponse rechargeBalance(String userId, BigDecimal amount) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User user = userEntity.toUser();
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(new UserEntity(user));
        return new BalanceResponse(user.getBalance().intValue(), user.getBalance().intValue());
    }

    @Override
    public BalanceResponse getBalance(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User user = userEntity.toUser();
        return new BalanceResponse(user.getBalance().intValue(), user.getBalance().intValue());
    }

    private int calculateQueuePosition(User user) {
        // 대기열 위치 계산 로직
        return 0; // Example placeholder
    }

    private int estimateWaitingTime(User user) {
        // 대기 시간 추정 로직
        return 0; // Example placeholder
    }
}
