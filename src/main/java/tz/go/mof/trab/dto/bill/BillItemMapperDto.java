package tz.go.mof.trab.dto.bill;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Augustino Mwageni
 */
@XmlRootElement(name = "BillItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillItemMapperDto implements Serializable{

	private static final long serialVersionUID = 267283741824699968L;
	
	@XmlElement(name = "BillItemRef")
	private String billItemReference;
	
	@XmlElement(name = "UseItemRefOnPay")
	private String useItemRefOnPay;
	
	@XmlElement(name = "BillItemAmt")
	private BigDecimal itemBilledAmount;
	
	@XmlElement(name = "BillItemEqvAmt")
	private BigDecimal itemEquivalentAmount;

	@XmlElement(name = "BillItemMiscAmt")
	private BigDecimal itemMiscellaneousAmount;

	@XmlElement(name = "GfsCode")
	private String gf;

	public BillItemMapperDto(){
		super();
	}
	
	
	/**
	 * @param billItemReference
	 * @param useItemRefOnPay
	 * @param itemBilledAmount
	 * @param itemEquivalentAmount
	 * @param itemMiscellaneousAmount
	 * @param gf
	 * @param itemMiscellaneousAmount 
	 *
	 */
	
	public BillItemMapperDto(String billItemReference, String useItemRefOnPay, BigDecimal itemBilledAmount,
			BigDecimal itemEquivalentAmount, String gf, BigDecimal itemMiscellaneousAmount) {
		super();
		this.billItemReference = billItemReference;
		this.useItemRefOnPay = useItemRefOnPay;
		this.itemBilledAmount = itemBilledAmount;
		this.itemEquivalentAmount = itemEquivalentAmount;
		this.itemMiscellaneousAmount = itemMiscellaneousAmount;
		this.gf = gf;
	}


	public String getBillItemReference() {
		return billItemReference;
	}


	public void setBillItemReference(String billItemReference) {
		this.billItemReference = billItemReference;
	}


	public String getUseItemRefOnPay() {
		return useItemRefOnPay;
	}


	public void setUseItemRefOnPay(String useItemRefOnPay) {
		this.useItemRefOnPay = useItemRefOnPay;
	}
	
	public BigDecimal getItemBilledAmount() {
		return itemBilledAmount;
	}


	public void setItemBilledAmount(BigDecimal itemBilledAmount) {
		this.itemBilledAmount = itemBilledAmount;
	}


	public BigDecimal getItemEquivalentAmount() {
		return itemEquivalentAmount;
	}


	public void setItemEquivalentAmount(BigDecimal itemEquivalentAmount) {
		this.itemEquivalentAmount = itemEquivalentAmount;
	}


	public BigDecimal getItemMiscellaneousAmount() {
		return itemMiscellaneousAmount;
	}


	public void setItemMiscellaneousAmount(BigDecimal itemMiscellaneousAmount) {
		this.itemMiscellaneousAmount = itemMiscellaneousAmount;
	}


	public String getGf() {
		return gf;
	}


	public void setGf(String gf) {
		this.gf = gf;
	}

	@Override
	public String toString() {
		return "BillItemMapperDto [billItemReference=" + billItemReference + ", useItemRefOnPay=" + useItemRefOnPay
				+ ", itemBilledAmount=" + itemBilledAmount + ", itemEquivalentAmount=" + itemEquivalentAmount
				+ ", itemMiscellaneousAmount=" + itemMiscellaneousAmount + ", gf=" + gf + "]";
	}
	
}
