package tz.go.mof.trab.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "BillTypes")
@NamedQuery(name = "BillTypes.findAll", query = "SELECT a FROM BillTypes a")
public class BillTypes {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long billTypeId;
	
	private String typeId;

	private String billTypeName;

	private BigDecimal amount;

	private Date createdDate;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "createdBy", nullable = false)
	private SystemUser systemUser;
	
	
	
}
