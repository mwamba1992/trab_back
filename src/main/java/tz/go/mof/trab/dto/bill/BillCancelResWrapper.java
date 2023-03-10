package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillCancelResWrapper {

	@XmlElement(name = "gepgBillCanclResp")
	private BIllCancelTrxRespo gepgBillCanclResp;

	@XmlElement(name = "gepgSignature")
	private String gepgSignature;

	public BIllCancelTrxRespo getGepgBillCanclResp() {
		return gepgBillCanclResp;
	}

	public void setGepgBillCanclResp(BIllCancelTrxRespo gepgBillCanclResp) {
		this.gepgBillCanclResp = gepgBillCanclResp;
	}

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}


	
}
