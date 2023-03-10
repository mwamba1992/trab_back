package tz.go.mof.trab.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;




/**
 * @author TERMIS-development team
 * @date June 11, 2020
 * @version 1.0.0
 */

@SuppressWarnings("deprecation")
@Configuration
public class AccountClientConfiguration {

	@Value("${security.oauth2.client.accessTokenUri}")
	private String accessTokenUri;


	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String secret;

	@Value("${security.oauth2.client.grant-type}")
	private String grantType;

	@Value("${security.oauth2.client.scope}")
	private String scope;

	private OAuth2ProtectedResourceDetails resource() {

		ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setAccessTokenUri(accessTokenUri);
		resourceDetails.setClientId(clientId);
		resourceDetails.setClientSecret(secret);
		resourceDetails.setGrantType(grantType);
		resourceDetails.setScope(Arrays.asList(scope.split(",")));

		return resourceDetails;
	}
}
