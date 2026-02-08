package tz.go.mof.trab.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthSecurityEventListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(OAuthSecurityEventListener.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY.AUDIT");
    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            this.handleSuccessfulAuthentication((AuthenticationSuccessEvent) event);
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            this.handleFailedAuthentication((AbstractAuthenticationFailureEvent) event);
        }
    }

    private void handleSuccessfulAuthentication(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof OAuth2Authentication) {
            OAuth2Authentication auth = (OAuth2Authentication) event.getAuthentication();
            String clientId = auth.getOAuth2Request().getClientId();
            String grantType = auth.getOAuth2Request().getGrantType();
            String username = auth.getUserAuthentication() != null ? auth.getUserAuthentication().getName() : "N/A";

            securityLogger.info("OAUTH_AUTH_SUCCESS - Time: {}, ClientId: {}, GrantType: {}, User: {}",
                    new Date(), clientId, grantType, username);
            this.failedAttempts.remove(clientId);
        }
    }

    private void handleFailedAuthentication(AbstractAuthenticationFailureEvent event) {
        String clientId = this.extractClientId(event);
        String reason = event.getException().getMessage();

        if (reason != null && reason.contains("client_credentials")) {
            securityLogger.error("CRITICAL_SECURITY_ALERT - Attempted client_credentials bypass! Time: {}, ClientId: {}, Reason: {}, Source: {}",
                    new Date(), clientId, reason, this.getSourceInfo(event));
            this.alertSecurityTeam(clientId, reason);
        }

        int attempts = this.failedAttempts.getOrDefault(clientId, 0) + 1;
        this.failedAttempts.put(clientId, attempts);

        securityLogger.warn("OAUTH_AUTH_FAILURE - Time: {}, ClientId: {}, Attempts: {}, Reason: {}",
                new Date(), clientId, attempts, reason);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            securityLogger.error("SECURITY_ALERT - Possible brute force attack! ClientId: {} has {} failed attempts",
                    clientId, attempts);
        }
    }

    private String extractClientId(AbstractAuthenticationFailureEvent event) {
        try {
            if (event.getAuthentication() instanceof OAuth2Authentication) {
                OAuth2Authentication auth = (OAuth2Authentication) event.getAuthentication();
                return auth.getOAuth2Request().getClientId();
            }
            return "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String getSourceInfo(AbstractAuthenticationFailureEvent event) {
        return "Check server logs for IP details";
    }

    private void alertSecurityTeam(String clientId, String reason) {
        logger.error("SECURITY TEAM ALERT: Attempted OAuth bypass for client: {} - {}", clientId, reason);
    }

    public Map<String, Integer> getFailedAttemptsReport() {
        return new HashMap<>(this.failedAttempts);
    }

    public void resetFailedAttempts(String clientId) {
        this.failedAttempts.remove(clientId);
        logger.info("Failed attempts reset for client: {}", clientId);
    }
}
