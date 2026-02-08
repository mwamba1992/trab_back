package tz.go.mof.trab.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import tz.go.mof.trab.service.CustomUserDetailsService;

import javax.sql.DataSource;

@EnableAuthorizationServer
@Configuration
@Primary
public class SecureAuthorizationServerConfiguration implements AuthorizationServerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SecureAuthorizationServerConfiguration.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY.AUDIT");

    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    public SecureAuthorizationServerConfiguration(PasswordEncoder passwordEncoder, DataSource dataSource,
                                                   CustomUserDetailsService customUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
        this.userDetailsService = customUserDetailsService;
    }

    @Bean
    @Primary
    public TokenStore secureJdbcTokenStore() {
        return new JdbcTokenStore(this.dataSource);
    }

    @Bean
    @Primary
    public ClientDetailsService clientDetailsService() {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(this.dataSource);
        clientDetailsService.setPasswordEncoder(this.passwordEncoder);
        return clientDetailsService;
    }

    @Bean
    PasswordEncoder clientPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("isAuthenticated()")
                .tokenKeyAccess("permitAll()")
                .passwordEncoder(this.clientPasswordEncoder());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(this.dataSource).passwordEncoder(this.passwordEncoder).build();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(this.secureJdbcTokenStore())
                .authenticationManager(this.authenticationManager)
                .userDetailsService((UserDetailsService) this.userDetailsService);
    }

    @Bean
    @Primary
    public DefaultTokenServices secureTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(this.secureJdbcTokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }
}
