package tz.go.mof.trab.dto.summon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for file upload operations via /summon/uploadFile endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "file")
public class FileUploadDto {
    private String file;      // Base64 encoded file content
    private String fileName;
}
