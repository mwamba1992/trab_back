package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcillitionRequestWrapper {

	@XmlElement(name = "gepgSpReconcReq")
	private ReconcillitionRequest gepgSpReconcReq;

	@XmlElement(name = "gepgSignature")
	private String gepgSignature;

	public ReconcillitionRequest getGepgSpReconcReq() {
		return gepgSpReconcReq;
	}

	public void setGepgSpReconcReq(ReconcillitionRequest gepgSpReconcReq) {
		this.gepgSpReconcReq = gepgSpReconcReq;
	}

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}

	
	
}
