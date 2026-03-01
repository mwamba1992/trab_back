package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for creating a new appeal via /appeal/internalCreate endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateAppealDto {
    private String invoiceNo;
    private String witnessList;  // JSON array string
    private String amountList;   // JSON array string
    private String assNo;
    private String bankNo;
    private String billNo;
    private String statement;
    private String taxedOffice;
    private String natureOfAppeal;
    private String phone;
    private String email;
    private String tinNumber;
    private String natOf;
    private String typeOfTax;
    private String region;
}
