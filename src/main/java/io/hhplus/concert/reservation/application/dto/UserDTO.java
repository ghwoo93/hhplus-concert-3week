package io.hhplus.concert.reservation.application.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String userId;
    private BigDecimal balance;
}
