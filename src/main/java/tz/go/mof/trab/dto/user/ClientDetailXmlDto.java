package tz.go.mof.trab.dto.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@RequiredArgsConstructor
public class ClientDetailXmlDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "ClientId")
	private String clientId;
	
	
	@XmlElement(name = "AccessTokenValidity")
	private String accessTokenValidity;
	
	
	@XmlElement(name = "Authorities")
	private String authorities;
	
	
	@XmlElement(name = "AuthorizedGrantTypes")
	private String authorizedGrantTypes;
	
	
	@XmlElement(name = "ClientSecret")
	private String clientSecret;
	
	@XmlElement(name = "ResourceId")
	private String resourceId;
	
	
	@XmlElement(name = "Scope")
	private String scope;
	
	
}
