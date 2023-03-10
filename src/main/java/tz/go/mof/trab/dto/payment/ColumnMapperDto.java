package tz.go.mof.trab.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ColumnMapperDto {

    private String controlNumber;

    private String pspReceipt;

    private String trxDateTime;

    private String currency;

    private String amount;

    private String  userId;
}
