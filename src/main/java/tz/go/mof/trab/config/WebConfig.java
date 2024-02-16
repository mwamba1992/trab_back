package tz.go.mof.trab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tz.go.mof.trab.utils.RateLimiterInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimiterInterceptor()).addPathPatterns("/application/**");
        registry.addInterceptor(new RateLimiterInterceptor()).addPathPatterns("/appeal/internalCreate");
        registry.addInterceptor(new RateLimiterInterceptor()).addPathPatterns("/notices/internalCreate");

    }
}

