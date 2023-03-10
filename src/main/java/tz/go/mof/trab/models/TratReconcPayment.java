package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Mwamba_Mwendavano
 * 
 * 
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "TratReconcPayment")
public class TratReconcPayment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long paymentId;

    private String trxId;

    private String payCtrNum;

    private String billAmt;

    private String paidAmt;

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
	private TratBill bill;

}
