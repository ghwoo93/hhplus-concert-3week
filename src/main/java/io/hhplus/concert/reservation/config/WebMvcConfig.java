package io.hhplus.concert.reservation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.hhplus.concert.reservation.presentation.interceptor.ApiInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ApiInterceptor apiInterceptor;

    @Autowired
    public WebMvcConfig(ApiInterceptor apiInterceptor) {
        this.apiInterceptor = apiInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiInterceptor)
                .addPathPatterns("/api/**"); // API 경로에만 인터셉터 적용
    }
}