package tz.go.mof.trab.dto.payment;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaymentSummaryDto {
    private String gfsName;
    private String gfsCode;
    private BigDecimal collectedAmount;
}
