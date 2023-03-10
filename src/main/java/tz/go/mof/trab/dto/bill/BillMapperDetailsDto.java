package tz.go.mof.trab.dto.bill;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Dickson
 *
 */

@Getter
@Setter
@ToString
@XmlRootElement(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillMapperDetailsDto implements Serializable  {

	private static final long serialVersionUID = -6344595244235484531L;
	
	@XmlElement(name="BillId")
	private String spBillId;
	
	@XmlElement(name="SubSpCode")
	private String subSpCode;
	
	@XmlElement(name="SpSysId")
	private String spSysId;
	
	@XmlElement(name="BillAmt")
	private BigDecimal billedAmount;
	
	@XmlElement(name="MiscAmt")
	private BigDecimal miscellaneousAmount;
	
	@XmlElement(name="BillExprDt")
	private String expiryDate;
	
	@XmlElement(name="PyrId")
	private String spPyrId;
	
	@XmlElement(name="PyrName")
	private String spPyrName;

	@XmlElement(name="BillDesc")
	private String billDescription;
	
	@XmlElement(name="BillGenDt")
	private String generatedDate;
	
	@XmlElement(name="BillGenBy")
	private String user;
	
	@XmlElement(name="BillApprBy")
	private String approvedBy;
	
	@XmlElement(name="PyrCellNum")
	private String payerPhone;
	
	@XmlElement(name="PyrEmail")
	private String payerEmail;
	
	@XmlElement(name="Ccy")
	private String currency;
	
	@XmlElement(name="BillEqvAmt")
	private BigDecimal billEquivalentAmount;
	
	@XmlElement(name="RemFlag")
	private boolean reminderFlag;
	
	@XmlElement(name="BillPayOpt")
	private Long billPayType;
	
	@XmlElement(name="BillItems")
	private BillItemsMapperDto billItems;

	public BillMapperDetailsDto() {
	}

	
}
