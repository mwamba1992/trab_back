package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BillReportDto {
    private String billId;

    private String controlNumber;

    private String paymentRef;

    private String serviceProviderCode;

    private String payerName;

    private String payerPhone;

    private String billDescription;

    private String billedItems;

    private Double totalBilledAmount;

    private String amountInWords;

    private String expireDate;

    private String preparedBy;

    private String printedBy;

    private String collectionCenter;

    private String printedDate;

    private String bankName;

    private String spName;

    private String swiftCode;

    private String accountNumber;

    private String qrString;

    private String printType;

    private String ccy;

    private String billExpiryDate;

    private String billCreatedDate;

    private String payerEmail;


}
