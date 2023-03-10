package tz.go.mof.trab.models;

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
@Table(name = "Taxes")
@NamedQuery(name = "Taxes.findAll", query = "SELECT a FROM Taxes a")
public class Taxes {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long  taxId;
	
    private String taxName;
    
    private String taxNo;
    
    private String description;
    
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "createdBy", nullable = false)
	private SystemUser systemUser;
	
	private Date createdDate;
	
}

