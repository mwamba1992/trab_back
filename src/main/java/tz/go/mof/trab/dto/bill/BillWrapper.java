package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillWrapper {

    @XmlElement(name="gepgBillSubReq")
    private BillMapperDto gepgBillSubReq;

    @XmlElement(name="gepgSignature")
    private String gepgSignature;

	public BillMapperDto getGepgBillSubReq() {
		return gepgBillSubReq;
	}

	public void setGepgBillSubReq(BillMapperDto gepgBillSubReq) {
		this.gepgBillSubReq = gepgBillSubReq;
	}

	public String getGepgSignature() {
		return gepgSignature;
	}

	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}
    
}