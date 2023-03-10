package tz.go.mof.trab.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BillItemsReportDto {
    @JsonProperty("governmentFinancialStatisticsDescription")
    private String governmentFinancialStatisticsDescription;

    @JsonProperty("itemBilledAmount")
    private String itemBilledAmount;

    @JsonProperty("governmentFinancialStatisticsCode")
    private String governmentFinancialStatisticsCode;


}
