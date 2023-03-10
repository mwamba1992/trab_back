package tz.go.mof.trab.dto.bill;

import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@ToString
public class PortalBillRequestDto {

    @Pattern(regexp = "255|0[0-9]{9}", message = "PhoneNumber Not Valid")
    private  String phoneNumber;

    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",message = "Email Not Valid")
    private String email;

    private String ccy;

    private String billDescription;

    private String PayerName;

    @Nullable
    private  String agentId;

    private List<PortalBillItemDto> billItems;


    @NotNull(message = "Request Type Shall Not be Null")
    private  String type;

    private String itemId;

    @Nullable
    private String createdBy;

    @Nullable
    private String approvedBy;

}
