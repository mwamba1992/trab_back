package tz.go.mof.trab.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import tz.go.mof.trab.security.CustomAccessDeniedHandler;
import tz.go.mof.trab.security.CustomAuthenticationEntryPoint;



@Configuration
//@EnableResourceServer
public class WebResourceConfiguration extends ResourceServerConfigurerAdapter {

	
	@Autowired
	private TokenStore tokenStore;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("termis-oauth2-resource").tokenStore(tokenStore);
		
		resources.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        resources.accessDeniedHandler(new CustomAccessDeniedHandler());
	}
}
