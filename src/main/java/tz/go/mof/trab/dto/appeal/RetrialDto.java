package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for registering appeal for retrial via /appeal/register-for-retrial endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RetrialDto {
    private String appealNo;
    private String taxType;
    private String reason;
}
