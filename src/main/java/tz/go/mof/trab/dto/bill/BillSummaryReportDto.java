package tz.go.mof.trab.dto.bill;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BillSummaryReportDto {
    private String dateFrom;
    private  String dateTo;
    private String councilCode;
    private String regionCode;

}
