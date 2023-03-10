package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcillitionResponseWrapper {


	
	@XmlElement(name = "gepgSpReconcResp")
	private ReconcillitionResponse reconcillitionResponse;

	@XmlElement(name = "gepgSignature")
	private String gepgSignature;

	public ReconcillitionResponse getReconcillitionResponse() {
		return reconcillitionResponse;
	}

	public void setReconcillitionResponse(ReconcillitionResponse reconcillitionResponse) {
		this.reconcillitionResponse = reconcillitionResponse;
	}

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}
	
	
}
