package tz.go.mof.trab.models;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import tz.go.mof.trab.utils.CustomGeneratedData;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Mwamba_Mwendavano
 * 
 * 
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity(name = "Payment")
public class Payment {

    @Id
    @Column(nullable = false, unique = true)
    private String paymentId = CustomGeneratedData.GenerateUniqueID();
	
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

    private Date trxDtm;
    
    @ManyToOne
	@JoinColumn(name = "billId", nullable = false)
	private Bill bill;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean gepgReconciled = false;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean pspReconciled = false;

    private String financialYear;

    private String action;

}
