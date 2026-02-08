package tz.go.mof.trab.dto.migration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DuplicateGroup {
    private String normalizedName;
    private List<String> originalVariants;
    private int appealCount;
}
