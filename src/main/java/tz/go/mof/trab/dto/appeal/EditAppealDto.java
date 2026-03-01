package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for editing an appeal via /appeal/appealEdit endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EditAppealDto {
    private String appealId;
    private String amountList;  // JSON array string
    private String date;
    private String assNo;
    private String bankNo;
    private String natOf;
    private String billNo;
    private String taxedOffice;
    private String natureOfAppeal;
    private String appealNo;
    private String appealantName;
    private String status;
    private String region;
}
