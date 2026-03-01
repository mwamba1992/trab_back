package tz.go.mof.trab.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for updating served by information via /application/update-served-by endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UpdateServedByDto {
    private String appNo;
    private String appName;
    private String appPhone;
    private String appDate;
    private String resoName;
    private String resoPhone;
    private String resoDate;
}
