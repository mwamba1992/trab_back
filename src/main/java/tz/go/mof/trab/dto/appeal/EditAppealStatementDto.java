package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for editing appeal statement/decision via /appeal/internalEdit endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "file")
public class EditAppealStatementDto {
    private String appealId;
    private String taxId;
    private String desicionDate;
    private String remarks;
    private String status;
    private String wonBy;
    private String fileName;
    private String judge;
    private String file;  // Base64 encoded file content
    private String aggregatedAppeals; // JSON array string of consolidated appeals with individual decision details
}
