package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.permission.RolePermissionsDto;
import tz.go.mof.trab.models.RolePermissions;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface RolePermissionService {

		public Response<RolePermissions> saveRolePermission(RolePermissionsDto rolePermissionDto);
		
		public ListResponse<RolePermissions> findByRoleId(String roleId);
		
		public ListResponse<RolePermissions> findByPermissionId(String permissionId);

		public Response<String> deleteByPermissionId(String permissionId);
		
		public Response<String> deleteByRoleId(String roleId);

		public Response<String> deleteRolePermission(RolePermissionsDto rolePermissionDto);

		public ListResponse<RolePermissions> getAllRolePermissions();	
	
}
