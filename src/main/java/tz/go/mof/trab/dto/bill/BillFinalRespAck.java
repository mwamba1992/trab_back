package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="gepgBillSubRespAck")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillFinalRespAck {

	@XmlElement(name = "TrxStsCode")
	private String trxStsCode;

	public String getTrxStsCode() {
		return trxStsCode;
	}

	public void setTrxStsCode(String trxStsCode) {
		this.trxStsCode = trxStsCode;
	}
	
	
	
}
