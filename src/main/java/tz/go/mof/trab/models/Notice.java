package tz.go.mof.trab.models;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString
@Entity
@Table(name = "Notice")
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long noticeId;
	
	@Temporal(TemporalType.DATE)
	private Date loggedAt;

	@Temporal(TemporalType.DATE)
	private Date dateOfTaxationDesicion;

	@Temporal(TemporalType.DATE)
	private Date dateOfServiceTaxationDesicion;

	@JsonIgnore
	@OneToOne(optional = true)
	@JoinColumn(name = "SystemUserId", nullable = false)
	private  SystemUser systemUser;
		
	private String  noticeNo;

	@OneToOne(optional = true)
	@JoinColumn(name = "billId", nullable = true)
	private  Bill billId;

	private String appelantName;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "adressId", nullable = true)
	private Adress adressId;
	
	private boolean isFilled;

	private String des;

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


}
