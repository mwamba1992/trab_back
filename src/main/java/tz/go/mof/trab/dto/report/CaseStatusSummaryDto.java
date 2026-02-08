package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CaseStatusSummaryDto {
    private String status;
    private int count;
    private double percentage;
    private double avgDaysInStatus;
}
