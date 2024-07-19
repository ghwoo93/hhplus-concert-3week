package io.hhplus.concert.reservation.presentation.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class CommonFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CommonFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        // 공통 보안 체크 (예: CORS 설정)
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");

        // 기본적인 인증 체크 (예: API 키 확인)
        String apiKey = httpRequest.getHeader("X-API-Key");
        if (apiKey == null || apiKey.isEmpty()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 요청 로깅
        logger.info("Incoming request - Method: {}, URI: {}, API Key: {}",
                httpRequest.getMethod(), httpRequest.getRequestURI(), apiKey);

        chain.doFilter(request, response);

        // 응답 로깅
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Outgoing response - Status: {}, Duration: {} ms",
                httpResponse.getStatus(), duration);
    }
}
