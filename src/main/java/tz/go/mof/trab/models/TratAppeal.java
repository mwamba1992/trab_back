package tz.go.mof.trab.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Trat_Appealss")
@Audited
public class TratAppeal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long tratNoticeId;
    private String noticeNo;
    private String tratAppealNo;
    private boolean appealFilled;
    private String status;
    private String decision;
    private String decidedBy;
    private boolean finished;


}
