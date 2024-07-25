package io.hhplus.concert.reservation.presentation.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.hhplus.concert.reservation.config.JwtConfig;
import io.hhplus.concert.reservation.domain.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiInterceptor.class);
    private final JwtConfig jwtConfig;

    @Autowired
    private TokenService tokenService;


    @Autowired
    public ApiInterceptor(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // /api/v1/tokens 엔드포인트는 인증 체크를 건너뜁니다.
        if (request.getRequestURI().equals("/api/v1/tokens")) {
            return true;
        }

        // 다른 API에 대한 인증 체크
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // Authorization 헤더 체크 (토큰이 필요한 다른 API들에 대해)
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String jwtToken = token.substring(7);
        String userId = extractUserIdFromToken(jwtToken);
        boolean isTokenValid = tokenService.isTokenValid(userId);

        if (userId == null || !isTokenValid) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.error("Invalid token {} {}", userId, isTokenValid);
            return false;
        }

        // API 호출 로깅
        logger.info("API Call - Method: {}, URI: {}, Token: {}", request.getMethod(), request.getRequestURI(), token);
        // request.setAttribute("userId", userId);
        return true;
    }

    private String extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Failed to extract userId from token", e);
            return null;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 컨트롤러 실행 후, 뷰 렌더링 전에 실행되는 로직
        // 필요한 경우 modelAndView를 수정하여 뷰에 전달되는 데이터를 가공할 수 있습니다.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // 뷰 렌더링 완료 후 실행되는 로직
        if (ex != null) {
            logger.error("Exception occurred while processing request: {}", ex.getMessage());
        }
    }
}