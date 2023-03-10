package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="BillCanclTrxDt")
@XmlAccessorType(XmlAccessType.FIELD)
public class BIllCancelRespo {
    
	@XmlElement(name="BillId")
	 private String billId;
	 
	 @XmlElement(name="TrxSts")
	 private String trxSts;

	 
	 @XmlElement(name="TrxStsCode")
	 private String trxStsCode;


	public String getBillId() {
		return billId;
	}


	public void setBillId(String billId) {
		this.billId = billId;
	}


	public String getTrxSts() {
		return trxSts;
	}


	public void setTrxSts(String trxSts) {
		this.trxSts = trxSts;
	}


	public String getTrxStsCode() {
		return trxStsCode;
	}


	public void setTrxStsCode(String trxStsCode) {
		this.trxStsCode = trxStsCode;
	}
	 
	 

}
