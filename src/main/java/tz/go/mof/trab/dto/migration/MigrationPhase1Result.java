package tz.go.mof.trab.dto.migration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MigrationPhase1Result {
    private int totalAppealsScanned;
    private int uniqueNamesFound;
    private int appellantsCreated;
    private int appellantsSkipped;
    private List<DuplicateGroup> duplicateVariants;
}
