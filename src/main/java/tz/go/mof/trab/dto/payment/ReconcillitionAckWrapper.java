package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcillitionAckWrapper {
	
	@XmlElement(name = "gepgSpReconcReqAck")
	private ReconcillitionAck gepgSpReconcReqAck;

	@XmlElement(name = "gepgSignature")
	private String gepgSignature;

	public ReconcillitionAck getGepgSpReconcReqAck() {
		return gepgSpReconcReqAck;
	}

	public void setGepgSpReconcReqAck(ReconcillitionAck gepgSpReconcReqAck) {
		this.gepgSpReconcReqAck = gepgSpReconcReqAck;
	}

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}

}
