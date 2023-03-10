package tz.go.mof.trab.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import tz.go.mof.trab.utils.CustomGeneratedData;


@Audited
@Entity
@Table(name = "Role")
@NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r")
@Getter
@Setter
@ToString
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id = CustomGeneratedData.GenerateUniqueID();

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "trr_role_permissions", joinColumns = {
			@JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {
			@JoinColumn(name = "permission_id", referencedColumnName = "id")})
	private List<Permission> permissions;

	@Basic(optional = false)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Basic(optional = true)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt= LocalDateTime.now();

	@Basic(optional = true)
	@Column(name = "created_by")
	private String createdBy;

	@Basic(optional = true)
	@Column(name = "updated_by")
	private String updatedBy;

}