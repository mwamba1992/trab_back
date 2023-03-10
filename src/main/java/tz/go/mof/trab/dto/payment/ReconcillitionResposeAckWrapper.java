package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcillitionResposeAckWrapper {

	
	@XmlElement(name = "gepgSpReconcRespAck")
	private ReconcillitionResponseAck gepgSpReconcRespAck;

	@XmlElement(name = "gepgSignature")
	private String gepgSignature;

	public ReconcillitionResponseAck getGepgSpReconcRespAck() {
		return gepgSpReconcRespAck;
	}

	public void setGepgSpReconcRespAck(ReconcillitionResponseAck gepgSpReconcRespAck) {
		this.gepgSpReconcRespAck = gepgSpReconcRespAck;
	}

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}
	
	
	
}
