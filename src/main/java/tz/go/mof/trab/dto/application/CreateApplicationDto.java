package tz.go.mof.trab.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for creating applications via /application/internalCreate endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "file")
public class CreateApplicationDto {
    private String type;
    private String email;
    private String phone;
    private String appeleantName;
    private String applicationType;
    private String natureOf;
    private String remarks;
    private String tax;
    private String slp;
    private String region;
    private String annextures;
}
