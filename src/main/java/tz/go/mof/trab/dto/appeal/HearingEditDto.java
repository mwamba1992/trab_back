package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for editing hearing info via /appeal/hearingEdit endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class HearingEditDto {
    private String appealId;
    private String type;
    private String concludingDate;
    private String dateDesicion;
    private String start;
    private String end;
    private String time;
}
