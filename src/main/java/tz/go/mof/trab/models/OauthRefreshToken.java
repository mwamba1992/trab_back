package tz.go.mof.trab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "oauth_refresh_token")
public class OauthRefreshToken {

	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "token_id")
	private String tokenId;

	@Column(name= "token", columnDefinition="blob")
	private String token;

	@Column(name = "authentication", columnDefinition="blob")
	private String authentication;

}
