package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class EnhancedAppealReportDto {
    private int serialNumber;
    private String appealNo;
    private String appellant;
    private String respondent;
    private String taxType;
    private Date filingDate;
    private Date decisionDate;
    private long daysOnTrial;
    private BigDecimal amountTzs = BigDecimal.ZERO;
    private BigDecimal amountUsd = BigDecimal.ZERO;
    private String status;
    private String remarks;
    private String judgeName;
    private String decidedBy;
}
