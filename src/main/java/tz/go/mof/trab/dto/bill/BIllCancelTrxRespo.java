package tz.go.mof.trab.dto.bill;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gepgBillCanclResp")
@XmlAccessorType(XmlAccessType.FIELD)
public class BIllCancelTrxRespo {
	
	@XmlElement(name = "BillCanclTrxDt")
	private List<BIllCancelRespo> gepgBillCanclResp;

	public List<BIllCancelRespo> getGepgBillCanclResp() {
		return gepgBillCanclResp;
	}

	public void setGepgBillCanclResp(List<BIllCancelRespo> gepgBillCanclResp) {
		this.gepgBillCanclResp = gepgBillCanclResp;
	}


}
