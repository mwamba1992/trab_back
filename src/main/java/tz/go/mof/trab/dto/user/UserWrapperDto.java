package tz.go.mof.trab.dto.user;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "Users")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@RequiredArgsConstructor
public class UserWrapperDto {
	
	@XmlElement(name = "UserDetail")
	private List<UserXmlDto> listOfUsers;

}
