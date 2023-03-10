package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QrCodeDto {
    private String opType;
    private String shortCode;
    private String billReference;
    private String amount;
    private String billCcy;
    private String billExprDt;
    private String billPayOpt;
    private String billRsv01;

}
