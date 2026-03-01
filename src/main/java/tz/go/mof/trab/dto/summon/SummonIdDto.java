package tz.go.mof.trab.dto.summon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for summon ID operations via /summon/internalDelete endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SummonIdDto {
    private String summonId;
}
