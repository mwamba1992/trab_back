package tz.go.mof.trab.models;


import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "decision_history")
@Audited
public class DecisionHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long decisionHistoryId;

    private Date decidedDate;

    @ManyToOne
    private AppealStatusTrend appealStatusTrend;

    private String judgeName;

    @Column(columnDefinition = "LONGBLOB")
    private String summaryOfDecree;

    private String hearingDate;


    @ManyToOne
    @JoinColumn(name = "appealId", nullable = false)
    private Appeals appeals;

    private String reason;


    private Date createdDate;

    private String createdBy;
}
