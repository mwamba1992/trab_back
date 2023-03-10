package tz.go.mof.trab.dto.bill;


import lombok.*;
import org.springframework.data.domain.Page;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageListResponse<T> {
    private boolean status;
    private int code;
    private Long totalElements;
    private Page<T> data;
}
