package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tz.go.mof.trab.dto.permission.RolePermissionsDto;
import tz.go.mof.trab.models.RolePermissions;
import tz.go.mof.trab.service.RolePermissionService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;



@RestController
@RequestMapping(value = "/api")

public class RolePermissionController {

	@Autowired
	private RolePermissionService rolePermissionService;

	@RequestMapping(value = "/saveRolePermission", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<RolePermissions> saveRolePermission(@RequestBody RolePermissionsDto rolePermissionDto) {

		return rolePermissionService.saveRolePermission(rolePermissionDto);
	}

	/**
	 * @param roleId Get permissions by using role id
	 * @return
	 */

	@RequestMapping(value = "/rolePermissions/permissions/{role-id}", method = RequestMethod.GET)

	public ListResponse<RolePermissions> getByRoleId(@PathVariable("role-id") String roleId) {

		return rolePermissionService.findByRoleId(roleId);
	}

	/**
	 * 
	 * @param permissionId Get roles by using permission id
	 * @return
	 */

	@RequestMapping(value = "/rolePermissions/roles/{permission-id}", method = RequestMethod.GET)

	public ListResponse<RolePermissions> getByPermissionId(@PathVariable("permission-id") String permissionId) {

		return rolePermissionService.findByPermissionId(permissionId);
	}

	/**
	 * Delete role permission by permission id
	 * 
	 * @param permissionId
	 * @return
	 */

	@RequestMapping(value = "/rolePermissions/permission/{permission-id}", method = RequestMethod.DELETE)

	public Response<String> deleteByPermissionId(@PathVariable("permission-id") String permissionId) {

		return rolePermissionService.deleteByPermissionId(permissionId);
	}

	/**
	 * Delete by Role Id
	 * 
	 * @param
	 * @return
	 */

	@RequestMapping(value = "/rolePermissions/role/{role-id}", method = RequestMethod.DELETE)

	public Response<String> deleteByRoleId(@PathVariable("role-id") String roleId) {

		return rolePermissionService.deleteByRoleId(roleId);
	}

	/**
	 * Delete Role Permission
	 * 
	 * @param permissionDto
	 * @return
	 */

	@RequestMapping(value = "/deletePermission", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<String> deletePermission(@RequestBody RolePermissionsDto permissionDto) {

		return rolePermissionService.deleteRolePermission(permissionDto);
	}

	/**
	 * Retrieve all role permissions
	 * 
	 * @return
	 */

	@RequestMapping(value = "/rolePermissions", method = RequestMethod.GET)
	public ListResponse<RolePermissions> getAllRolePermissions() {

		return rolePermissionService.getAllRolePermissions();
	}

}
