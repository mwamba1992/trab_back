package tz.go.mof.trab.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.models.ApplicationRegister;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReportResponseDto {
    private boolean success;
    private  String  message;
    private List<ApplicationRegister> applicationRegisters;
    private List<Appeals> appeals;
}
