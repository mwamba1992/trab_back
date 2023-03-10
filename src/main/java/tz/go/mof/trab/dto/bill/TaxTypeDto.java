package tz.go.mof.trab.dto.bill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaxTypeDto {


    @NotNull
    private String taxName;

    @NotNull
    private  String taxDesc;
}
