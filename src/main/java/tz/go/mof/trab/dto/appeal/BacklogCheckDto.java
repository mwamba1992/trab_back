package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for checking if backlog appeal exists via /appeal/load-backlog-appeal endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BacklogCheckDto {
    private String region;
    private String appealNo;
    private String tax;
}
