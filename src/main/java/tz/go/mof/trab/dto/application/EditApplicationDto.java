package tz.go.mof.trab.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for editing applications via /application/internalEdit endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "file")
public class EditApplicationDto {
    private String tax;
    private String appNo;
    private String wonBy;
    private String decidedBy;
    private String remarks;
    private String statusTrend;
    private String date;
    private String fileName;
    private String file;      // Base64 encoded file content
}
