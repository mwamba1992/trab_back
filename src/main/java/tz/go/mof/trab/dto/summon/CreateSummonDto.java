package tz.go.mof.trab.dto.summon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for creating/editing summons via /summon/internalCreate and /summon/internalEdit endpoints
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateSummonDto {
    private String summoId;      // Used for editing existing summon
    private String startDate;
    private String endDate;
    private String judge;
    private String appList;      // JSON array string
    private String venue;
    private String time;
    private String memberOne;
    private String memberTwo;
    private String drawnByAdress;
    private String drawnByName;
    private String name;         // "Appeals" or "Applications"
}
