package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ApplicationDto {
    String applicationNo;
    String appeleantName;
    String respondent;
    String decidedBy;
    String natureOfRequest;
    Date dateOfFilling;
    Date dateOfDecision;
    String tax;
    String decision;
    String status;
}
