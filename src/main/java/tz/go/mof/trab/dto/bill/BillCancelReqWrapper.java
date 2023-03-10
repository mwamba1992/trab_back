package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillCancelReqWrapper {

	@XmlElement(name = "gepgBillCanclReq")
	private BillCancelReq billCancelReq;

	@XmlElement(name = "gepgSignature")
	private String gepgSignature;

	public BillCancelReq getBillCancelReq() {
		return billCancelReq;
	}

	public void setBillCancelReq(BillCancelReq billCancelReq) {
		this.billCancelReq = billCancelReq;
	}

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}
	
	

}
