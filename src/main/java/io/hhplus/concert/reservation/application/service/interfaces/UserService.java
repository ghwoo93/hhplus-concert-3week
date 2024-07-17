package io.hhplus.concert.reservation.application.service.interfaces;


import java.math.BigDecimal;

import io.hhplus.concert.reservation.application.dto.TokenDTO;
import io.hhplus.concert.reservation.presentation.response.BalanceResponse;

public interface UserService {
    TokenDTO generateToken(String userId);
    BalanceResponse rechargeBalance(String userId, BigDecimal amount);
    BalanceResponse getBalance(String userId);
}
