package tz.go.mof.trab.models;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import tz.go.mof.trab.utils.CustomGeneratedData;

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
@Table(name = "Region")
public class Region {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id = CustomGeneratedData.GenerateUniqueID();

	@Column(nullable =  true, name = "name",unique = false)
	private String name;

	@Column(nullable =  true, name = "region_code",unique = true)
	private String code;

}
