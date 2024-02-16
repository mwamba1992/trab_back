package tz.go.mof.trab.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;



public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiter rateLimiter = new RateLimiter(10, TimeUnit.SECONDS);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (rateLimiter.tryAcquire()) {
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return false;
        }
    }
}
