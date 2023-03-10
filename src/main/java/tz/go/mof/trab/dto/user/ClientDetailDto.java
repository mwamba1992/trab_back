package tz.go.mof.trab.dto.user;

import java.io.Serializable;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@RequiredArgsConstructor
public class ClientDetailDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private String clientId;

	private String resourceIds;

	private String clientSecret;

	private String scope;

	private String authorizedGrantTypes;

	private String webServerRedirectUri;

	private String authorities;

	private String accessTokenValidity;

	private String refreshTokenValidity;

	private String additionalInformation;

	private String autoapprove;

	
	
}
