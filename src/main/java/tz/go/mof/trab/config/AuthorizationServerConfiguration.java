package tz.go.mof.trab.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import tz.go.mof.trab.service.CustomUserDetailsService;


@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfiguration implements AuthorizationServerConfigurer {

	private final DataSource dataSource;
	private final PasswordEncoder passwordEncoder;

	private final CustomUserDetailsService userDetailsService;

	private final WebResponseExceptionTranslator exceptionTranslator;

	@Autowired
	AuthenticationManager authenticationManager;

	@Bean
	public TokenStore jdbcTokenStore() {
		return new JdbcTokenStore(dataSource);
	}


	AuthorizationServerConfiguration(PasswordEncoder passwordEncoder, DataSource dataSource,
									 CustomUserDetailsService customUserDetailsService, WebResponseExceptionTranslator exceptionTranslator) {
		this.passwordEncoder = passwordEncoder;
		this.dataSource = dataSource;
		this.userDetailsService = customUserDetailsService;
		this.exceptionTranslator = exceptionTranslator;
	}

	@Bean
	PasswordEncoder clientPasswordEncoder() {
		return new BCryptPasswordEncoder(4);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {

		security.checkTokenAccess("isAuthenticated()").tokenKeyAccess("permitAll()");
		security.passwordEncoder(clientPasswordEncoder());

		// Added
		security.allowFormAuthenticationForClients();

	}


	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients.jdbc(dataSource).passwordEncoder(passwordEncoder);

	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.tokenStore(jdbcTokenStore());
		endpoints.authenticationManager(authenticationManager);
		endpoints.userDetailsService(userDetailsService);


		endpoints.exceptionTranslator(exceptionTranslator);

	}

	@Bean
	public DefaultTokenServices defaultTokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(jdbcTokenStore());
		return defaultTokenServices;
	}

}