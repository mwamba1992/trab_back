package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for loading backlog appeals manually via /appeal/load-backlog endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BacklogAppealDto {
    private String region;
    private String appealNo;
    private String tax;
    private String appellantName;
    private String dateFilling;
    private String decidedDate;
    private String statusTrend;
    private String decidedBy;
    private String summary;
    private String tin;
    private String phone;
    private String nature;
    private String amountList;  // JSON array string
}
