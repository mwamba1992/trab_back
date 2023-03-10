package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * @author Mwamba_Mwendavano
 * 
 * 
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "ReconcPayment")
public class ReconcPayment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long paymentId;
	
    private String trxId;
    
    private String payCtrNum;
    
    private BigDecimal billAmt;
    
    private BigDecimal paidAmt;
    
    private String usdPayChnl;
    
    private String payRefId;
    
    private String pyrCellNum;
    
    private String pyrName;
    
    private String pspReceiptNumber;
    
    private String pspName;

    private String ctrAccNum;
   
    private String createdDate;

    @ManyToOne
	@JoinColumn(name = "billId", nullable = false)
	private Bill bill;

}
