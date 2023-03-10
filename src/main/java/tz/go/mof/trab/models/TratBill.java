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

/**
 * @author Mwamba_Mwendavano
 * 
 * 
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "TratBill")
@NamedQuery(name = "TratBill.findAll", query = "SELECT b FROM TratBill b")
public class TratBill {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long billId;

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
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "createdBy", nullable = false)
	private SystemUser systemUser;
	
	private Date receivedTime;
	
	@JsonIgnore
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "currencyId", nullable = false)
	private Currency currency;

}
