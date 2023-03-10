package tz.go.mof.trab.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

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
@Table(name = "Witness")
@NamedQuery(name = "Witness.findAll", query = "SELECT w FROM Witness w")
public class Witness {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long witnessId;
	
	private String fullName;
	
	private String phoneNumber;
	
}
