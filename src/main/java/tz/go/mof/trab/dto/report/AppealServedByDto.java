package tz.go.mof.trab.dto.report;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class AppealServedByDto {

    private String appName;
    private String phoneNumber;

    private String respoName;
    private String respoPhone;

    private String appealNo;

    private String date;

}
