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
public class UserXmlDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "Name")
	private String name;
			
	@XmlElement(name = "Email")
	private String email;
	
	@XmlElement(name = "Sex")
	private String sex;
	
	
	@XmlElement(name = "CheckNumber")
	private String checkNumber;
	
	
	@XmlElement(name = "Password")
	private String password;  
	
	 

}
