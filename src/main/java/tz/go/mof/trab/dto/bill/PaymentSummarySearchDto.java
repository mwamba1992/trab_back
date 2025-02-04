package tz.go.mof.trab.dto.bill;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSummarySearchDto {

    private String appType;

    private int month;

    private String year;
}
