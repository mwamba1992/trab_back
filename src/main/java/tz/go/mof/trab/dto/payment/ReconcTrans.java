package tz.go.mof.trab.dto.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReconcTrans")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcTrans {

	@XmlElement(name = "ReconcTrxInf")
	List<ReconTransactionInfo> reconTransactionInfo;

	public List<ReconTransactionInfo> getReconcTrxInf() {
		return reconTransactionInfo;
	}

	public void setReconcTrxInf(List<ReconTransactionInfo> reconTransactionInfo) {
		this.reconTransactionInfo = reconTransactionInfo;
	}
	
	
	
}
