package tz.go.mof.trab.dto.bill;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BillSearchDto {
    private  String controlNumber;
    private String dateFrom;
    private  String dateTo;
    private  String amountFrom;
    private  String amountTo;
    private  String payerName;
    private String status;
    private String  sourceId;
    private String regionCode;
    private  String councilCode;

}

