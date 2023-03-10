package tz.go.mof.trab.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author termis-development team
 * @date June 02, 2020
 * @version 1.0.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

	private Boolean status;

	private Integer code;
	
	private String description;

	private T data;

}
