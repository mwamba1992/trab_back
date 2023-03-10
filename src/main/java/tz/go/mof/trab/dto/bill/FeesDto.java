package tz.go.mof.trab.dto.bill;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;



@Getter
@Setter
@ToString
public class FeesDto {

    private String  revenueName;

    private BigDecimal amount;

    private String gfsId;


}
