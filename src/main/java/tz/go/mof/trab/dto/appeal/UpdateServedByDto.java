package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for updating served-by info via /appeal/update-served-by endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"file1", "file2"})
public class UpdateServedByDto {
    private String appNo;
    private String tax;
    private String appName;
    private String appPhone;
    private String appDate;
    private String resoName;
    private String resoPhone;
    private String resoDate;
    private String file1;      // Base64 encoded file content
    private String fileName1;
    private String file2;      // Base64 encoded file content
    private String fileName2;
}
