package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for bill search criteria via /api/bills-dynamic/ endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BillDynamicDto {
    private String status;
    private String startDate;
    private String endDate;
}
