package tz.go.mof.trab.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import tz.go.mof.trab.utils.CustomGeneratedData;

/**
 * @author Mwamba_Mwendavano
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Bill")
@NamedQuery(name = "Bill.findAll", query = "SELECT b FROM Bill b")
public class Bill {


    @Id
    @Column(nullable = false, unique = true)
    private String billId = CustomGeneratedData.GenerateUniqueID();

    private String approvedBy;

    private String billDescription;

    private boolean billPayed;

    private String billReference;

    @NotNull
    private BigDecimal billedAmount;

    private String billControlNumber;

    private BigDecimal billEquivalentAmount;

    private String appType;

    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date generatedDate;

    private Timestamp lastReminder;

    @NotNull
    private BigDecimal miscellaneousAmount;

    private String payerEmail;

    private String remarks;

    private String payerPhone;

    private String payerName;

    private boolean reminderFlag;

    @Column(nullable = true)
    private String spSystemId;

    private String billPayType;

    private Date receivedTime;

    private String currency;

    private String status;

    private  String itemId;

    private double debtCount = 0;

    @ColumnDefault("0")
    private BigDecimal paidAmount;

	private String createdBy;

	private String financialYear;

	private String responseCode;

	private String action;

}
