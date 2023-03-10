package tz.go.mof.trab.dto.payment;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Getter
@Setter
@XmlRootElement(name = "ReconcBatchInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconBatchInfo {

	@XmlElement(name = "SpReconcReqId")
	private String spReconcReqId;

	@XmlElement(name = "SpCode")
	private String spCode;

	@XmlElement(name = "SpName")
	private String spName;

	@XmlElement(name = "ReconcStsCode")
	private String reconcStsCode;

}
