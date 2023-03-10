package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.PermissionDto;
import tz.go.mof.trab.models.Permission;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface PermissionService {
	
    public Response<Permission> savePermission(PermissionDto permissionDto);
	
	public Response<Permission> findByName(String name);
	
	public Response<Permission> findByPermissionId(String permissionId);
		
	public Response<Permission> updatePermission(PermissionDto permissionDto);
	
	public Response<Permission> deletePermission(String permissionId);
	
	public ListResponse<Permission> getAllPermissions();

		
}
