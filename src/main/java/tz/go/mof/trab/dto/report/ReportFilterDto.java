package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReportFilterDto {
    private String dateFrom;
    private String dateTo;
    private String dateOfDecisionFrom;
    private String dateOfDecisionTo;
    private String taxType;
    private String statusTrend;
    private String judgeId;
    private String financialYear;
    private String progressStatus;
    private String isTribunal;
    private Integer minDaysOpen = 90;
    private String format = "pdf";
    private String region;
    private String wonBy;
    private String chairPerson;
    private String hearingStage;
}
