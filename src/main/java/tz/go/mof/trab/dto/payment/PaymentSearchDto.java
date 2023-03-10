package tz.go.mof.trab.dto.payment;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaymentSearchDto {
    private  String controlNumber;
    private String dateFrom;
    private  String dateTo;
    private  String payerName;
    private String pspReference;
    private String gepgReference;
    private  String councilCode;
    private  String regionCode;

}
