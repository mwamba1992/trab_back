package tz.go.mof.trab.dto.permission;

import java.io.Serializable;
import java.util.List;

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
public class PermissionCategoryXmlDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "Name")
	private String name;

	@XmlElement(name = "Active")
	private Boolean active;
	
	@XmlElement(name = "Permissions")
	private List<PermissionXmlDto> permissions;

}
