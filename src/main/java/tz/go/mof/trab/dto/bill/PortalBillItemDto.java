package tz.go.mof.trab.dto.bill;


import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PortalBillItemDto {


    private BigDecimal billedAmount;

    private String sourceName;

    private String  gsfCode;


}
