package tz.go.mof.trab.dto.permission;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "PermissionDetails")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@RequiredArgsConstructor
public class PermissionWrapperDto {
	
	@XmlElement(name = "PermissionCategory")
	private List<PermissionCategoryXmlDto> listOfPermissionCategory;
		   
	@XmlElement(name = "Permissions")   
	private List<PermissionXmlDto> listOfPermissions;

}
