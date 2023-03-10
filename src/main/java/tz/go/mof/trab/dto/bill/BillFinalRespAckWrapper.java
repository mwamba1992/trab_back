package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillFinalRespAckWrapper {
	
	@XmlElement(name="gepgBillSubRespAck")
    private BillFinalRespAck billFinalAck;
	
	 @XmlElement(name="gepgSignature")
	 private String gepgSignature;
	 
	 

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}

	public BillFinalRespAck getBillFinalAck() {
		return billFinalAck;
	}

	public void setBillFinalAck(BillFinalRespAck billFinalAck) {
		this.billFinalAck = billFinalAck;
	}
	
	
	
}
