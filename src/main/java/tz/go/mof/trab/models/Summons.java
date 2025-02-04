package tz.go.mof.trab.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

/**
 * @author Mwamba_Mwendavano
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Summons")
@NamedQuery(name = "Summons.findAll", query = "SELECT a FROM Summons a")

public class Summons {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long summonId;

    private String filePath;

    private String summonNo;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "createdBy", nullable = false)
    private SystemUser systemUser;

    private String appeleant;

    private String respondent;

    private String appeleantAdress;

    private String respondentAdress;

    private String judge;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "jud", nullable = false)
    private Judge jud;

    private String venue;

    @Temporal(TemporalType.DATE)
    private Date summonStartDate;

    @Temporal(TemporalType.DATE)
    private Date summonEndDate;

    @Temporal(TemporalType.DATE)
    private Date receivedAt;

    private boolean isReceived;

    private String time;

    private String appList;

    private String memberOne;

    private String memberTwo;

    private String taxType;

    private String summonType;

    private String drawnBy;


    @OneToMany(mappedBy = "summons", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JudgeHistory> judgeHistory;

}
