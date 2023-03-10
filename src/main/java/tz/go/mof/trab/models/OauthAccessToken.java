package tz.go.mof.trab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "oauth_access_token")
public class OauthAccessToken {

	@Column(name = "token_id")
	private String tokenId;

	@Column(columnDefinition="blob")
	private String token;

	@Id
	@Column(name = "authentication_id")
	private String authenticationId;

	@Column(name = "user_name")
	private String username;

	@Column(name = "client_id")
	private String clientId;

	@Column(columnDefinition="blob")
	private String authentication;

	@Column(name = "refresh_token")
	private String refreshToken;
}
