package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for searching payments via /appeal/search-payments endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaymentSearchDto {
    private String controlNumber;
    private String pspRef;
    private String bank;
    private String startDate;
    private String endDate;
}
