package tz.go.mof.trab.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name = "Appellant")
@NamedQuery(name = "Appellant.findAll", query = "SELECT a FROM Appellant a")
public class Appellant {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long AppellantId;
	
	private String firstName;

	private String lastName;

	private String natureOfBusiness;
	
	private String phoneNumber;
	
    private String email;
    
    private String tinNumber = "NONE";
    
    private String  incomeTaxFileNumber = "NONE";
    
    private String vatNumber = "NONE";
    
	@Temporal(TemporalType.DATE)
	private Date createdDate;

}
