package tz.go.mof.trab.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mof.trab.models.Appeals;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseAgeAnalysisDTO {
    Appeals appeals;
    private Integer ageInDays;
}
