package io.hhplus.concert.reservation.presentation.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 세부적인 인증 체크 (예: 토큰 유효성 검사)
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // API 호출 로깅
        logger.info("API Call - Method: {}, URI: {}, Token: {}",
                request.getMethod(), request.getRequestURI(), token);

        return true;
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