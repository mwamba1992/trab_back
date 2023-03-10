package tz.go.mof.trab.service;

import org.springframework.validation.BindingResult;
import tz.go.mof.trab.dto.bill.RoleDto;
import tz.go.mof.trab.models.Role;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface RoleService {
	
    public Response<Role> saveRole(RoleDto roleDto);
	
	public Response<Role> findByName(String name);

	public Response<Role> findById(String id);

	public Response<Role> updateRole(RoleDto roleDto, String roleId, BindingResult result);
	
	public Response<Role> deleteRole(String roleId);
	
	public ListResponse<Role> getAllRoles();

		
}
