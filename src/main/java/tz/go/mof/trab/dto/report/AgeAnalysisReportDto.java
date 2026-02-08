package tz.go.mof.trab.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgeAnalysisReportDto {
    private int dayFrom;
    private int dayTo;
    private String proceedingStatus;
}
