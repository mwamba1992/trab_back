package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for fetching appeals by date range via /appeal/appeals-date endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppealDateRangeDto {
    private String dateFrom;
    private String dateTo;
    private String tax;
}
