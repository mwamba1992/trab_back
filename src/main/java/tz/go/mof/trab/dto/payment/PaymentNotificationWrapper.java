package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentNotificationWrapper {
	

    @XmlElement(name="gepgPmtSpInfo")
    private PaymentNotification pymentNot;
    

    @XmlElement(name="gepgSignature")
    private String gepgSignature;


	public PaymentNotification getPymentNot() {
		return pymentNot;
	}


	public void setPymentNot(PaymentNotification pymentNot) {
		this.pymentNot = pymentNot;
	}


	public String getGepgSignature() {
		return gepgSignature;
	}


	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}
    
    
    
    

}
