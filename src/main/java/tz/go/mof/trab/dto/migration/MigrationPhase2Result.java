package tz.go.mof.trab.dto.migration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MigrationPhase2Result {
    private int totalAppealsProcessed;
    private int appealsLinked;
    private int appealsAlreadyLinked;
    private int appealsUnmatched;
    private List<String> unmatchedAppealNos;
}
