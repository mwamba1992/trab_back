package tz.go.mof.trab.dto.report;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ApplicationServedByDto {

    private String appName;
    private String phoneNumber;

    private String respoName;
    private String respoPhone;
}
