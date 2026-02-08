package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OutstandingBillDto {
    private int serialNumber;
    private String controlNumber;
    private String billReference;
    private String appType;
    private String payerName;
    private String currency;
    private BigDecimal billedAmount = BigDecimal.ZERO;
    private Date generatedDate;
    private Date expiryDate;
    private long agingDays;
    private String agingBucket;
}
