package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaymentReportDto {

    private int serialNumber;

    private String pspReceiptNumber;

    private String payerName;

    private BigDecimal paidAmount;

    private String billId;

    private String controlNumber;

    private String transactionDate;

    private Date paymentDate;

    private String pspName;

    private String appType;

    private String issuedBy;

    private String issuedDate;

    private String amountInWords;

    private String outstandingBalance;
}
