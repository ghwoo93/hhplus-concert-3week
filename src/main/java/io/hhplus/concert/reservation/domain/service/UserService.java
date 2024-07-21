package io.hhplus.concert.reservation.domain.service;

import java.math.BigDecimal;

import io.hhplus.concert.reservation.domain.model.User;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;

public interface UserService {
    boolean existsById(String userId);
    BalanceResponse rechargeBalance(String userId, BigDecimal amount);
    BalanceResponse getBalance(String userId);
    User getUser(String userId);
    BalanceResponse deductBalance(String userId, BigDecimal amount);
}
