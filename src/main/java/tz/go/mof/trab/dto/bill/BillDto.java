package tz.go.mof.trab.dto.bill;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tz.go.mof.trab.models.BillItems;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class BillDto {

	private String billId;

	private String spPayerName;

	private String billDescription;

	private String payerPhone;

	private String payerEmail;

	private String currency;

	private Double exchangeRateValue;

	private long billPayType;

	private Date expiryDate;

	private boolean reminderFlag;

	private Double billedAmount;

	private Double miscellaneousAmount;
	
	private List<BillItems> billItems = new ArrayList<BillItems>();
	
	private List<Double> itemBilledAmount; 
	
	private List<Double> itemMiscellaneousAmount;
	
	private List<Long> revenueSource; 
	
	private List<String> billReference; 
	
	private String billControlNumber;
	
	private String generatedDate;
	
	private String payerName;
	
	private Double exchangeRate;
	
	private Long paymentType;
	
	private Date expireDate;
	
	private Double amountPaid;
	
	private Date paymentDate;
	
	private String pspName;
	
	private String spName;
	
	private String creator;
	
	private Long transAccountId;


}
