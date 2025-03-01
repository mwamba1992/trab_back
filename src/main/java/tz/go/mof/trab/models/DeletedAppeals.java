package tz.go.mof.trab.models;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

/**
 * @author Joel M Gaitan
 *
 *
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "deleted_appeals")
@ToString
public class DeletedAppeals {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long appealId;

    private String appealNo;

    @Temporal(TemporalType.DATE)
    private Date dateOfFilling;

    private String natureOfAppeal;


    @Column(columnDefinition = "LONGBLOB")
    private String remarks;

    private String taxedOff;

    private String assNo;

    private String billNo;

    private String bankNo;

    private String wonBy;

    private String status;

    private String noticeNumber;

    private String currencyOfAmountOnDispute;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    private Date approvedDate;

    private String outcomeOfDecision;


    @Temporal(TemporalType.DATE)
    private Date decidedDate;


    private String  decidedBy;

    private String appellantName;


    @OneToOne(optional = true)
    @JoinColumn(name = "bill", nullable = true)
    private  Bill billId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "taxId", nullable = false)
    private TaxType tax;


    @ManyToOne(optional = false)
    @JoinColumn(name = "statusTrend", nullable = false)
    private AppealStatusTrend statusTrend;


    @Temporal(TemporalType.DATE)
    private Date dateOfTheLastOrder;


    @Temporal(TemporalType.DATE)
    private Date concludingDate;


    private String procedingStatus;



    @Column(columnDefinition = "LONGBLOB")
    private String summaryOfDecree;


    private String copyOfJudgement;


    private String decreeReceivedBy;

    private Boolean isFilledTrat;

    @JsonIgnore
    @OneToOne(optional = true)
    @JoinColumn(name = "served", nullable = true)
    private  AppealServedBy appealServedBy;


    private String action;


    @Column(name = "created_by", nullable = true)
    private String createdBy;

    @Basic(optional = false)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Basic(optional = false)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "updated_by",nullable = true)
    private String updatedBy;

    private String tinNumber;

    private String email;

    private String phone;

    private String natOfBus;

    private boolean isLoaded;


}
