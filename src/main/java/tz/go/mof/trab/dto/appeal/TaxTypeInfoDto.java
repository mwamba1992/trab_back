package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for tax type info statistics response from /appeal/gettaxinfo endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaxTypeInfoDto {
    private String name;
    private String amtTZS;
    private String amtUSD;
    private String allTZS;
    private String allUSD;
}
