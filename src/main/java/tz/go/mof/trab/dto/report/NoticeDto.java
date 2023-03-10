package tz.go.mof.trab.dto.report;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NoticeDto {
    private Date loggedAt;
    private String  noticeNo;
    private  String appealantName;
    private  String appealantTin;
    private String adress;
    private String createdBy;
}
