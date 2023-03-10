package tz.go.mof.trab.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Joel M Gaitan
 * 
 * 
 */

@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Adress")
@NamedQuery(name = "Adress.findAll", query = "SELECT a FROM Adress a")
public class Adress {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long adressId;
	
    private String slp;
  
	@ManyToOne(optional = true)
	@JoinColumn(name = "regionId", nullable = true)
	private Region region;
    

}
