package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="gepgBillSubReqAck")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillAck {
	
	@XmlElement(name = "TrxStsCode")
	private String trxStsCode;

	public String getTrxStsCode() {
		return trxStsCode;
	}

	public void setTrxStsCode(String trxStsCode) {
		this.trxStsCode = trxStsCode;
	}

	@Override
	public String toString() {
		return "BillAckCode [trxStsCode=" + trxStsCode + "]";
	}
}
