package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gepgSpReconcRespAck")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcillitionResponseAck {
	

	@XmlElement(name = "ReconcStsCode")
	private String reconcStsCode;

	public String getReconcStsCode() {
		return reconcStsCode;
	}

	public void setReconcStsCode(String reconcStsCode) {
		this.reconcStsCode = reconcStsCode;
	}
	
	

}
