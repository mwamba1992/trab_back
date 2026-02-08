package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JudgeWorkloadReportDto {
    private String judgeName;
    private int totalCases;
    private int pendingCases;
    private int hearingCases;
    private int concludedCases;
    private int decidedCases;
    private double avgDaysToDecision;
    private long oldestCaseDays;
}
