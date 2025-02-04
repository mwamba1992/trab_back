package tz.go.mof.trab.models;


import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "judge_history")
@Audited
public class JudgeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "summonId", nullable = false)
    private Summons summons;

    @ManyToOne
    @JoinColumn(name = "jud", nullable = false)
    private Judge judge;

    @Temporal(TemporalType.DATE)
    private Date changeDate;

}
