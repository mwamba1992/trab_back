package tz.go.mof.trab.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
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
@Table(name = "Evidence")
@NamedQuery(name = "Evidence.findAll", query = "SELECT a FROM Evidence a")
public class Evidence {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long evidenceId;
	
	private String evidenceType;
	
	private String evidenceRemarks;
	
	private String fileName;
	
}
