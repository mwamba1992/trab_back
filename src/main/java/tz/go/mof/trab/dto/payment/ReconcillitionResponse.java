package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gepgSpReconcResp")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcillitionResponse {

	@XmlElement(name = "ReconcBatchInfo")
	private ReconBatchInfo reconcBatchInfo;

	@XmlElement(name = "ReconcTrans")
   private ReconcTrans reconcTrxInf;

	public ReconBatchInfo getReconcBatchInfo() {
		return reconcBatchInfo;
	}

	public void setReconcBatchInfo(ReconBatchInfo reconcBatchInfo) {
		this.reconcBatchInfo = reconcBatchInfo;
	}

	public ReconcTrans getReconcTrxInf() {
		return reconcTrxInf;
	}

	public void setReconcTrxInf(ReconcTrans reconcTrxInf) {
		this.reconcTrxInf = reconcTrxInf;
	}
	
	
}
