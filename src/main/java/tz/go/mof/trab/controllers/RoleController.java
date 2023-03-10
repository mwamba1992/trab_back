package tz.go.mof.trab.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tz.go.mof.trab.dto.bill.RoleDto;
import tz.go.mof.trab.models.Role;
import tz.go.mof.trab.service.RoleService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;


@RestController
@RequestMapping(value = "/api")

public class RoleController {

	@Autowired
	private RoleService roleService;

	@RequestMapping(value = "/role", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<Role> saveRole(@RequestBody RoleDto roleDto) {

		return roleService.saveRole(roleDto);
	}

	/**
	 * Get role by role id
	 *
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/role/{role-id}", method = RequestMethod.GET)

	public Response<Role> findById(@PathVariable("role-id") String roleId) {

		return roleService.findById(roleId);
	}

	/**
	 * Get role by role id
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/role/name/{role-name}", method = RequestMethod.GET)

	public Response<Role> findByName(@PathVariable("role-name") String name) {

		return roleService.findByName(name);
	}

	/**
	 * DELETE role by role id
	 *
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/role/{role-id}", method = RequestMethod.DELETE)

	public Response<Role> deleteByRoleId(@PathVariable("role-id") String roleId) {

		return roleService.deleteRole(roleId);
	}

	@RequestMapping(value = "/role/{role-id}", method = RequestMethod.PUT)
	@ResponseBody
	public Response<Role> updateRole(@PathVariable("role-id") String roleId, @RequestBody @Valid RoleDto roleDto,
									  BindingResult result) {

		return roleService.updateRole(roleDto, roleId, result);

	}


	@RequestMapping(value = "/roles", method = RequestMethod.GET)
	public ListResponse<Role> getAllRoles() {

		return roleService.getAllRoles();
	}

}
