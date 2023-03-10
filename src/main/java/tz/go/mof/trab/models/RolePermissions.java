package tz.go.mof.trab.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;
import org.hibernate.envers.Audited;


@Audited
@Entity
@Table(name = "trr_role_permissions")
@IdClass(RolePermissionsId.class)
@Data
public class RolePermissions{

	@Id    
	@Column(name="role_id")
	private String roleId;
    
	@Id
	@Column(name="permission_id")
    private String permissionId;
	
	
}