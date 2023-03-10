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
public class GfsDto {

    @NotNull
    private String gfsCode;

    @NotNull
    private  String gfsName;
}
