package io.hhplus.concert.reservation.application.service.interfaces;

import io.hhplus.concert.reservation.domain.model.Token;

public interface TokenService {
    /**
     * 새로운 토큰을 생성합니다.
     *
     * @param userId 토큰을 생성할 사용자의 ID
     * @return 생성된 Token 객체
     */
    Token createToken(String userId);

    /**
     * 주어진 토큰 문자열에 해당하는 Token 객체를 조회합니다.
     *
     * @param token 조회할 토큰 문자열
     * @return 조회된 Token 객체
     * @throws TokenNotFoundException 토큰을 찾을 수 없는 경우
     */
    Token getToken(String token);

    /**
     * 주어진 토큰을 무효화합니다.
     *
     * @param token 무효화할 토큰 문자열
     */
    void invalidateToken(String token);

    /**
     * 토큰의 유효성을 검사합니다.
     *
     * @param token 검사할 토큰 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    boolean isTokenValid(String token);
}