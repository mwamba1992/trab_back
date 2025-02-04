package tz.go.mof.trab.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeListAppeal {

    private Long noticeNoticeId;  // Adjust according to your primary key

    private String listAppeal;
}
