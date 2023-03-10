package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaymentReportDto {

    private String pspReceiptNumber;

    private String payerName;

    private BigDecimal paidAmount;

    private String billId;

    private  String controlNumber;

    private String transactionDate;

    private String issuedBy;

    private String issuedDate;

    private  String amountInWords;

    private String outstandingBalance;


}
