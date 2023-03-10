package tz.go.mof.trab.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PymtTrxInf")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentTransactionInfo {

	@XmlElement(name = "TrxId")
	private String trxId;

	@XmlElement(name = "SpCode")
	private String spCode;

	@XmlElement(name = "PayRefId")
	private String payRefId;

	@XmlElement(name = "BillId")
	private String billId;

	@XmlElement(name = "PayCtrNum")
	private String payCtrNum;

	@XmlElement(name = "BillAmt")
	private String billAmt;

	@XmlElement(name = "PaidAmt")
	private String paidAmt;

	@XmlElement(name = "BillPayOpt")
	private String billPayOpt;

	@XmlElement(name = "CCy")
	private String cCy;

	@XmlElement(name = "TrxDtTm")
	private String trxDtTm;

	@XmlElement(name = "UsdPayChnl")
	private String usdPayChnl;

	@XmlElement(name = "PyrCellNum")
	private String pyrCellNum;

	@XmlElement(name = "PyrEmail")
	private String pyrEmail;

	@XmlElement(name = "PyrName")
	private String pyrName;

	@XmlElement(name = "PspReceiptNumber")
	private String pspReceiptNumber;

	@XmlElement(name = "PspName")
	private String pspName;

	@XmlElement(name = "CtrAccNum")
	private String ctrAccNum;

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

	public String getSpCode() {
		return spCode;
	}

	public void setSpCode(String spCode) {
		this.spCode = spCode;
	}

	public String getPayRefId() {
		return payRefId;
	}

	public void setPayRefId(String payRefId) {
		this.payRefId = payRefId;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getPayCtrNum() {
		return payCtrNum;
	}

	public void setPayCtrNum(String payCtrNum) {
		this.payCtrNum = payCtrNum;
	}

	public String getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(String billAmt) {
		this.billAmt = billAmt;
	}

	public String getPaidAmt() {
		return paidAmt;
	}

	public void setPaidAmt(String paidAmt) {
		this.paidAmt = paidAmt;
	}

	public String getBillPayOpt() {
		return billPayOpt;
	}

	public void setBillPayOpt(String billPayOpt) {
		this.billPayOpt = billPayOpt;
	}

	public String getcCy() {
		return cCy;
	}

	public void setcCy(String cCy) {
		this.cCy = cCy;
	}

	public String getTrxDtTm() {
		return trxDtTm;
	}

	public void setTrxDtTm(String trxDtTm) {
		this.trxDtTm = trxDtTm;
	}

	public String getUsdPayChnl() {
		return usdPayChnl;
	}

	public void setUsdPayChnl(String usdPayChnl) {
		this.usdPayChnl = usdPayChnl;
	}

	public String getPyrCellNum() {
		return pyrCellNum;
	}

	public void setPyrCellNum(String pyrCellNum) {
		this.pyrCellNum = pyrCellNum;
	}

	public String getPyrEmail() {
		return pyrEmail;
	}

	public void setPyrEmail(String pyrEmail) {
		this.pyrEmail = pyrEmail;
	}

	public String getPyrName() {
		return pyrName;
	}

	public void setPyrName(String pyrName) {
		this.pyrName = pyrName;
	}

	public String getPspReceiptNumber() {
		return pspReceiptNumber;
	}

	public void setPspReceiptNumber(String pspReceiptNumber) {
		this.pspReceiptNumber = pspReceiptNumber;
	}

	public String getPspName() {
		return pspName;
	}

	public void setPspName(String pspName) {
		this.pspName = pspName;
	}

	public String getCtrAccNum() {
		return ctrAccNum;
	}

	public void setCtrAccNum(String ctrAccNum) {
		this.ctrAccNum = ctrAccNum;
	}
	
	

}
