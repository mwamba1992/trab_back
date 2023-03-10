package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillRespWrapper {
	
	@XmlElement(name="gepgBillSubResp")
	BillResp billResp;
	

    @XmlElement(name="gepgSignature")
    private String gepgSignature;


	public BillResp getBillResp() {
		return billResp;
	}


	public void setBillResp(BillResp billResp) {
		this.billResp = billResp;
	}


	public String getGepgSignature() {
		return gepgSignature;
	}


	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}

 

}
