package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gepgPmtSpInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentNotification {
	
	@XmlElement(name="PymtTrxInf")
	private PaymentTransactionInfo pymntTRxInfo;

	public PaymentTransactionInfo getPymntTRxInfo() {
		return pymntTRxInfo;
	}

	public void setPymntTRxInfo(PaymentTransactionInfo pymntTRxInfo) {
		this.pymntTRxInfo = pymntTRxInfo;
	}
	
	

}
