package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for loading backlog pending appeals with summons via /appeal/load-backlog-pending endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BacklogPendingDto {
    private String startDate;
    private String endDate;
    private String lastOrderDate;
    private String judge;
    private String appList;     // JSON array string
    private String memberOne;
    private String memberTwo;
}
