package tz.go.mof.trab.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "currency")
public class Currency extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String currencyShortName;
	
	private String currencyDescription;
	
	private Double exchangeRate;


}
