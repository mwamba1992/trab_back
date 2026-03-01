package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Simple DTO for single appeal number/identifier operations
 * Used by /appeal/find-by-appeal-no and /appeal/update-filled-trat endpoints
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppealNoDto {
    private String no;            // For find-by-appeal-no
    private String appealNumber;  // For update-filled-trat
}
