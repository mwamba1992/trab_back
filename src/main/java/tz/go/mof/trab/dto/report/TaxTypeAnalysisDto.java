package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TaxTypeAnalysisDto {
    private String taxType;
    private int appealCount;
    private int applicationCount;
    private int totalCases;
    private int pendingCases;
    private int decidedCases;
    private BigDecimal totalAmountTzs = BigDecimal.ZERO;
}
