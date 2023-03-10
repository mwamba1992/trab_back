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
public class AdministrativeAreaXmlDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "RegionCode")
	private String regionCode;
	
	@XmlElement(name = "RegionName")
	private String regionName;
	
	@XmlElement(name = "LGACode")
	private String lGACode;
	
	@XmlElement(name = "LGAName")
	private String lGAName;
	
	@XmlElement(name = "WardCode")
	private String wardCode;
	
	
	@XmlElement(name = "WardName")
	private String wardName;
	
	@XmlElement(name = "VillageCode")
	private String villageCode;
	
	@XmlElement(name = "VillageName")
	private String villageName;
	
	
}
