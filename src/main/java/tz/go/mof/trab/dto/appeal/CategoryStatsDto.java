package tz.go.mof.trab.dto.appeal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for category statistics response from /appeal/getCategory endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoryStatsDto {
    private String name;
    private String y;
    private String color;
}
