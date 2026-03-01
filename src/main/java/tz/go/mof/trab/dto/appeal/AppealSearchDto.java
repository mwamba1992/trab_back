package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for searching appeals via /appeal/internalSearch endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppealSearchDto {
    private String statusId;
    private String token;
}
