package tz.go.mof.trab.dto.bill;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BankAccountDto {

    private String accountNumber;

    private String bankAccountName;

    private String bankName;

    private String ccyId;

}
