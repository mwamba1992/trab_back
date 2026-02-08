package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OverdueCaseDto {
    private String appealNo;
    private String appellant;
    private String respondent;
    private String taxType;
    private Date filingDate;
    private long daysOpen;
    private String agingBucket;
    private String progressStatus;
    private String judgeName;
}
