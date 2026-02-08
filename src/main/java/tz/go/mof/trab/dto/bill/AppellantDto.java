package tz.go.mof.trab.dto.bill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppellantDto {
    private String firstName;
    private String lastName;
    private String natureOfBusiness;
    private String phoneNumber;
    private String email;
    private String tinNumber;
    private String incomeTaxFileNumber;
    private String vatNumber;
}
