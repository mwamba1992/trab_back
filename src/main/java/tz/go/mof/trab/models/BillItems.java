package tz.go.mof.trab.models;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "BillItem")
@NamedQuery(name = "BillItems.findAll", query = "SELECT b FROM BillItems b")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BillItems {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long billItemRefId;
	 
	private String billItemRef;
	
	private BigDecimal billItemAmount;
	
	private BigDecimal billItemMiscAmount;
	
	private BigDecimal billItemEqvAmount;
	
	private String billItemDescription;

	private String sourceName;

	private String gsfCode;
	
	@ManyToOne
	@JoinColumn(name = "billId", nullable = false)
	private Bill bill;
	

}
