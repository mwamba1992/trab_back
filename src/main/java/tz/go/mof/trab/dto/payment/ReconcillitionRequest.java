package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="gepgSpReconcReq")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconcillitionRequest {


    @XmlElement(name="SpReconcReqId")
    private String spReconcReqId;
    

    @XmlElement(name="SpCode")
    private String spCode;
    
    @XmlElement(name="SpSysId")
    private String spSysId;
    
    @XmlElement(name="TnxDt")
    private String tnxDt;
    
    @XmlElement(name="ReconcOpt")
    private String reconcOpt;

	public String getSpReconcReqId() {
		return spReconcReqId;
	}

	public void setSpReconcReqId(String spReconcReqId) {
		this.spReconcReqId = spReconcReqId;
	}

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

	public String getTnxDt() {
		return tnxDt;
	}

	public void setTnxDt(String tnxDt) {
		this.tnxDt = tnxDt;
	}

	public String getReconcOpt() {
		return reconcOpt;
	}

	public void setReconcOpt(String reconcOpt) {
		this.reconcOpt = reconcOpt;
	}
    
    
}
