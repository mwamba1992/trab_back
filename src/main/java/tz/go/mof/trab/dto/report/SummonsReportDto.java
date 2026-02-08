package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SummonsReportDto {
    private int serialNumber;
    private String summonNo;
    private Date startDate;
    private Date endDate;
    private String judgeName;
    private String memberOne;
    private String memberTwo;
    private String venue;
    private String time;
    private String linkedCases;
    private String summonType;
}
