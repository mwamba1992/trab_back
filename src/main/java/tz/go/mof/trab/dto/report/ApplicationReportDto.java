package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationReportDto {
    private int serialNumber;
    private String applicationNo;
    private String applicantName;
    private String respondentName;
    private String taxType;
    private Date filingDate;
    private Date decisionDate;
    private String progressStatus;
}
