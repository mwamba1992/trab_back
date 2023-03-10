package tz.go.mof.trab.models;



import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import tz.go.mof.trab.utils.CustomGeneratedData;
import java.io.Serializable;
import java.time.LocalDateTime;


@Audited
@Getter
@Setter
@Entity
@Table(name = "Permission")
@NamedQuery(name = "Permission.findAll", query = "SELECT p FROM Permission p")
public class Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id = CustomGeneratedData.GenerateUniqueID();

	@Column(name = "name")
	private String name;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "service_name")
	private String serviceName;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "deleted")
	private Boolean deleted;

	//@ManyToOne
	@Column(name="permission_category_id", nullable=false)
	private String permissionCategory ;

	@Basic(optional = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Basic(optional = true)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();
	
}