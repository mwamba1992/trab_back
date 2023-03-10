package tz.go.mof.trab.models;

import java.math.BigDecimal;
import javax.persistence.*;
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
@Table(name = "fees")
public class Fees extends BaseEntity{

	private String  revenueName;

	private BigDecimal amount;

	@ManyToOne
	@JoinColumn(name = "gfsId", nullable = false)
	private Gfs gfs;


}
