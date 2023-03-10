package tz.go.mof.trab.models;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;


@Getter
@Setter
@NoArgsConstructor
@ToString
@Embeddable
public class RolePermission {
	
	@ManyToOne
	private Permission Permission;

	@ManyToOne
	private Role Role;
	
	@Column(nullable = false)
	private Long RecordCreatedBy;

	@Column(nullable = false)
	private Date RecordCreatedDate;

}
