package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Gepg")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentResponseWrapper {

	@XmlElement(name="gepgPmtSpInfoAck")
    private PaymentResponse paymentResponse;
	

    @XmlElement(name="gepgSignature")
    private String gepgSignature;


	public PaymentResponse getPaymentResponse() {
		return paymentResponse;
	}


	public void setPaymentResponse(PaymentResponse paymentResponse) {
		this.paymentResponse = paymentResponse;
	}


	public String getGepgSignature() {
		return gepgSignature;
	}


	public void setGepgSignature(String gepgSignature) {
		this.gepgSignature = gepgSignature;
	}

    
  
}
