package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for top appellant statistics response from /appeal/gettopappelant endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TopAppellantDto {
    private String name;
    private String amtTZS;
    private String amtUSD;
    private String allTZS;
    private String allUSD;
    private String desicion;
}
