package tz.go.mof.trab.dto.bill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class CurrencyDto {

    @Size(min = 3 ,max = 3)
    private String currencyShortName;

    @NotNull
    private String currencyDescription;

    @NotNull
    @DecimalMin(value = "1.0")
    private Double exchangeRate;

}
