package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for notice filter criteria via /api/notices-dynamic/format/{format} endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NoticeFilterDto {
    private String dateFrom;
    private String dateTo;
}
