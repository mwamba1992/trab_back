package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReconcTrxInf")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReconTransactionInfo {

	@XmlElement(name = "SpBillId")
	private String spBillId;

	@XmlElement(name = "BillCtrNum")
	private String billCtrNum;

	@XmlElement(name = "pspTrxId")
	private String pspTrxId;

	@XmlElement(name = "PaidAmt")
	private String paidAmt;

	@XmlElement(name = "CCy")
	private String cCy;

	@XmlElement(name = "PayRefId")
	private String payRefId;

	@XmlElement(name = "TrxDtTm")
	private String trxDtTm;

	@XmlElement(name = "CtrAccNum")
	private String ctrAccNum;

	@XmlElement(name = "UsdPayChnl")
	private String ssdPayChnl;

	@XmlElement(name = "PspName")
	private String pspName;

	@XmlElement(name = "PspCode")
	private String pspCode;

	@XmlElement(name = "DptCellNum")
	private String DptCellNum;

	@XmlElement(name = "DptName")
	private String dptName;

	@XmlElement(name = "DptEmailAddr")
	private String dptEmailAddr;

	@XmlElement(name = "Remarks")
	private String remarks;

	@XmlElement(name = "ReconcRsv1")
	private String reconcRsv1;

	@XmlElement(name = "ReconcRsv1")
	private String reconcRsv2;

	@XmlElement(name = "ReconcRsv1")
	private String reconcRsv3;

	public String getSpBillId() {
		return spBillId;
	}

	public void setSpBillId(String spBillId) {
		this.spBillId = spBillId;
	}

	public String getBillCtrNum() {
		return billCtrNum;
	}

	public void setBillCtrNum(String billCtrNum) {
		this.billCtrNum = billCtrNum;
	}

	public String getPspTrxId() {
		return pspTrxId;
	}

	public void setPspTrxId(String pspTrxId) {
		this.pspTrxId = pspTrxId;
	}

	public String getPaidAmt() {
		return paidAmt;
	}

	public void setPaidAmt(String paidAmt) {
		this.paidAmt = paidAmt;
	}

	public String getcCy() {
		return cCy;
	}

	public void setcCy(String cCy) {
		this.cCy = cCy;
	}

	public String getPayRefId() {
		return payRefId;
	}

	public void setPayRefId(String payRefId) {
		this.payRefId = payRefId;
	}

	public String getTrxDtTm() {
		return trxDtTm;
	}

	public void setTrxDtTm(String trxDtTm) {
		this.trxDtTm = trxDtTm;
	}

	public String getCtrAccNum() {
		return ctrAccNum;
	}

	public void setCtrAccNum(String ctrAccNum) {
		this.ctrAccNum = ctrAccNum;
	}

	public String getSsdPayChnl() {
		return ssdPayChnl;
	}

	public void setSsdPayChnl(String ssdPayChnl) {
		this.ssdPayChnl = ssdPayChnl;
	}

	public String getPspName() {
		return pspName;
	}

	public void setPspName(String pspName) {
		this.pspName = pspName;
	}

	public String getPspCode() {
		return pspCode;
	}

	public void setPspCode(String pspCode) {
		this.pspCode = pspCode;
	}

	public String getDptCellNum() {
		return DptCellNum;
	}

	public void setDptCellNum(String dptCellNum) {
		DptCellNum = dptCellNum;
	}

	public String getDptName() {
		return dptName;
	}

	public void setDptName(String dptName) {
		this.dptName = dptName;
	}

	public String getDptEmailAddr() {
		return dptEmailAddr;
	}

	public void setDptEmailAddr(String dptEmailAddr) {
		this.dptEmailAddr = dptEmailAddr;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReconcRsv1() {
		return reconcRsv1;
	}

	public void setReconcRsv1(String reconcRsv1) {
		this.reconcRsv1 = reconcRsv1;
	}

	public String getReconcRsv2() {
		return reconcRsv2;
	}

	public void setReconcRsv2(String reconcRsv2) {
		this.reconcRsv2 = reconcRsv2;
	}

	public String getReconcRsv3() {
		return reconcRsv3;
	}

	public void setReconcRsv3(String reconcRsv3) {
		this.reconcRsv3 = reconcRsv3;
	}
	
	
	

}
