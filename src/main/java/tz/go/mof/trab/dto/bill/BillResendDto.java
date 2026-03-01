package tz.go.mof.trab.dto.bill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for resending bill via /api/bill-resend-bill endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BillResendDto {
    private String billId;
}
