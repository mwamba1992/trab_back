package tz.go.mof.trab.dto.permission;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@XmlRootElement(name = "PermissionCategory")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PermissionXmlDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "Name")
	private String name;
			
	@XmlElement(name = "DisplayName")
	private String displayName;
	
	@XmlElement(name = "ServiceName")
	private String serviceName;
	
	
	@XmlElement(name = "Active")
	private Boolean active;
	
	
	@XmlElement(name = "Deleted")	
	private Boolean deleted; 

}
