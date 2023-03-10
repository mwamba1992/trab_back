package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="gepgBillSubResp")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillResp {
	
	 @XmlElement(name="BillTrxInf")
	 private BillRespBillTrxInf trx;

	public BillRespBillTrxInf getTrx() {
		return trx;
	}

	public void setTrx(BillRespBillTrxInf trx) {
		this.trx = trx;
	}
	 
	 

}
