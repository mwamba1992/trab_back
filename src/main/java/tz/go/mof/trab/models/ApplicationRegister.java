package tz.go.mof.trab.models;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * @author Mwamba_Mwendavano
 * 
 * 
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ApplicationRegister")
public class ApplicationRegister {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applicationId;

	private String applicationNo;

	@Temporal(TemporalType.DATE)
	private Date dateOfFilling;

	@Temporal(TemporalType.DATE)
	private Date dateOfDecision;

	private String natureOfRequest;


	@ManyToOne(optional = true)
	@JoinColumn(name = "notice", nullable = true)
	private Notice notice;

	private String decideBy;

	@ManyToOne(optional = false)
	@JoinColumn(name = "taxId", nullable = false)
	private TaxType taxes;;

	@ManyToOne(optional = false)
	@JoinColumn(name = "statusTrend", nullable = false)
	private ApplicationStatusTrend statusTrend;

	@Column(columnDefinition="LONGBLOB")
	private String remarks;


	@OneToOne(optional = true)
	@JoinColumn(name = "billId", nullable = true)
	private  Bill billId;

	private String type;

	@ManyToOne(optional = true)
	@JoinColumn(name = "summonId", nullable = true)
	private Summons summons;

	@ManyToOne(optional = true)
	@JoinColumn(name = "applicantId", nullable = true)
	private Appellant applicant;

	@ManyToOne(optional = true)
	@JoinColumn(name = "respondentId", nullable = true)
	private Respondent respondent;

	private String wonBy;

	private  String filePath;

	@Column(columnDefinition = "LONGBLOB")
	private byte[] desicionSummary;

	@JsonIgnore
	@OneToOne(optional = true)
	@JoinColumn(name = "served", nullable = true)
	private  ApplicationServedBy applicationServedBy;

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

	@OneToOne(optional = false)
	@JoinColumn(name = "adressId", nullable = true)
	private Adress adressId;

}
