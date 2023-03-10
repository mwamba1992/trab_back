package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillAckWrapper {

    @XmlElement(name="gepgBillSubReqAck")
    private BillAck billAckCode;
    

    @XmlElement(name="gepgSignature")
    private String gepgSignature;


	public BillAck getBillAckCode() {
		return billAckCode;
	}


	public void setBillAckCode(BillAck billAckCode) {
		this.billAckCode = billAckCode;
	}


	public String getGepgSignature() {
		return gepgSignature;
	}


	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}
  
   
}