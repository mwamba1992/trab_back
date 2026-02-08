package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TopAppellantDto {
    private int rank;
    private String appellantName;
    private int totalCases;
    private int pendingCount;
    private int decidedCount;
    private String taxTypes;
    private BigDecimal totalAmountTzs = BigDecimal.ZERO;
}
