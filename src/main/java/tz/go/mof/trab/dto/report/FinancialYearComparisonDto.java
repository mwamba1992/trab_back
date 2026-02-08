package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FinancialYearComparisonDto {
    private int serialNumber;
    private String financialYear;
    private int appealsCount;
    private int applicationsCount;
    private int totalCases;
    private int decidedCount;
    private int pendingCount;
}
