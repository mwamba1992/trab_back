package tz.go.mof.trab.dto.bill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="gepgBillCanclReq")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillCancelReq {
	
	 @XmlElement(name="SpCode")
	 private String spCode;
	 
	 @XmlElement(name="SpSysId")
	 private String spSysId;

	 
	 @XmlElement(name="BillId")
	 private String billId;


	public String getSpCode() {
		return spCode;
	}


	public void setSpCode(String spCode) {
		this.spCode = spCode;
	}


	public String getSpSysId() {
		return spSysId;
	}


	public void setSpSysId(String spSysId) {
		this.spSysId = spSysId;
	}


	public String getBillId() {
		return billId;
	}


	public void setBillId(String billId) {
		this.billId = billId;
	}
	 
	 

	 
	

}
