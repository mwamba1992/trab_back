package tz.go.mof.trab.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;

@Component
public class OAuthEndpointSecurityInterceptor implements HandlerInterceptor {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY.OAUTH");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/oauth/token")) {
            String grantType = request.getParameter("grant_type");
            String clientId = request.getParameter("client_id");
            String clientIP = this.getClientIP(request);
            String userAgent = request.getHeader("User-Agent");

            securityLogger.info("OAUTH_TOKEN_REQUEST - Time: {}, IP: {}, Client: {}, Grant: {}, UserAgent: {}",
                    LocalDateTime.now(), clientIP, clientId, grantType, userAgent);

            if ("client_credentials".equals(grantType)) {
                securityLogger.warn("CLIENT_CREDENTIALS_ATTEMPT - Time: {}, IP: {}, Client: {}, Headers: {}",
                        LocalDateTime.now(), clientIP, clientId, this.getRequestHeaders(request));

                if ("termis-client".equals(clientId) || "termis-mobile-client".equals(clientId)) {
                    securityLogger.error("SECURITY_VIOLATION_DETECTED - User-facing client '{}' attempting client_credentials from IP: {} at {}",
                            clientId, clientIP, LocalDateTime.now());
                }
            }

            this.detectSuspiciousPatterns(request, clientId, clientIP, grantType);
        }
        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("X-Real-IP");
        }
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getRemoteAddr();
        }
        return clientIP;
    }

    private String getRequestHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.toLowerCase().contains("authorization") || headerName.toLowerCase().contains("secret")) {
                continue;
            }
            headers.append(headerName).append(":").append(request.getHeader(headerName)).append(";");
        }
        return headers.toString();
    }

    private void detectSuspiciousPatterns(HttpServletRequest request, String clientId, String clientIP, String grantType) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            String lowerAgent = userAgent.toLowerCase();
            if (lowerAgent.contains("curl") || lowerAgent.contains("wget") ||
                    lowerAgent.contains("python") || lowerAgent.contains("postman") ||
                    lowerAgent.contains("insomnia")) {
                securityLogger.warn("AUTOMATED_TOOL_DETECTED - IP: {}, Client: {}, UserAgent: {}, Grant: {}",
                        clientIP, clientId, userAgent, grantType);
            }
        }

        String referer = request.getHeader("Referer");
        if (referer == null && !"client_credentials".equals(grantType)) {
            securityLogger.warn("MISSING_REFERER - Potentially direct API access from IP: {}, Client: {}",
                    clientIP, clientId);
        }

        String contentType = request.getContentType();
        if (contentType != null && !contentType.contains("application/x-www-form-urlencoded")) {
            securityLogger.warn("UNUSUAL_CONTENT_TYPE - IP: {}, Client: {}, ContentType: {}",
                    clientIP, clientId, contentType);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (request.getRequestURI().contains("/oauth/token")) {
            String clientId = request.getParameter("client_id");
            String grantType = request.getParameter("grant_type");
            String clientIP = this.getClientIP(request);

            securityLogger.info("OAUTH_TOKEN_RESPONSE - Client: {}, Grant: {}, IP: {}, Status: {}, Time: {}",
                    clientId, grantType, clientIP, response.getStatus(), LocalDateTime.now());

            if (response.getStatus() >= 400) {
                securityLogger.warn("OAUTH_TOKEN_ERROR - Client: {}, Grant: {}, IP: {}, Status: {}, Error: {}",
                        clientId, grantType, clientIP, response.getStatus(),
                        ex != null ? ex.getMessage() : "HTTP_ERROR");
            }
        }
    }
}
