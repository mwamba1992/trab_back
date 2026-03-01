package tz.go.mof.trab.dto.audit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for fetching audit logs via /audit/logs endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AuditLogFilterDto {
    private String table;
    private String dateFrom;
    private String dateTo;
}
