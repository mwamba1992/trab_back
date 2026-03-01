package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for date range filter via /api/notices-dates and /api/applications-dates endpoints
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DateRangeDto {
    private String dateFrom;
    private String dateTo;
}
