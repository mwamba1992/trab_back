package tz.go.mof.trab.dto.report;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class AppealDto {
    String appealNo;
    private Date dateOfFilling;
    private String natureOfAppeal;
    private String  decidedBy;
    private Date decidedDate;
    private String appealant;
    private String respondent;
    private String amountDetails;
    private BigDecimal tzs;
    private BigDecimal usd;
    private String tax;
    private String findings;
    private String remarks;

}
