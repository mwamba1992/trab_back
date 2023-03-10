package tz.go.mof.trab.utils;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ListResponse<T> {
	private boolean status;
	private int code;
	private Long totalElements;
	private List<T> data;
	private String description;
}
