package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class NoticeReportDto {
    private int serialNumber;
    private String noticeNo;
    private String appellantName;
    private String description;
    private Date noticeDate;
    private String controlNumber;
    private String paymentStatus;
    private String financialYear;
}
