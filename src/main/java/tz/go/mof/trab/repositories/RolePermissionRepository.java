package tz.go.mof.trab.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.go.mof.trab.models.RolePermissions;
import tz.go.mof.trab.models.RolePermissionsId;


@Repository

public interface RolePermissionRepository extends JpaRepository<RolePermissions, RolePermissionsId>{


	
	List<RolePermissions> findByRoleId(String roleId);
	
	List<RolePermissions> findByPermissionId(String permissionId);
	
	void deleteByPermissionId(String permissionId);
	
	void deleteByRoleId(String roleId);
	
}
