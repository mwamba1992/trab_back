package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for application filter criteria via /api/applications-dynamic/format/{format} endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApplicationFilterDto {
    private String hearing;
    private String tax;
    private String region;
    private String applicationTrendType;
    private String dateFrom;
    private String dateTo;
    private String decidedDateFrom;
    private String decidedDateTo;
    private String chairMan;
    private String wonBy;
}
