package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tz.go.mof.trab.dto.bill.UserRoleDto;
import tz.go.mof.trab.models.UserRole;
import tz.go.mof.trab.service.UserRoleService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;


@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = {"*"})
public class UserRoleController {

	@Autowired
	private UserRoleService userRoleService;

	/**
	 * Add/Save user role
	 * 
	 * @param userRoleDto
	 * @return //
	 */


	@RequestMapping(value = "/userRole", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<UserRole> savePermission(@RequestBody UserRoleDto userRoleDto) {

		return userRoleService.saveUserRole(userRoleDto);
	}

	/**
	 * Get user role by role id
	 * 
	 * @param roleId
	 * @return
	 */


	@RequestMapping(value = "/userRole/role/{role-id}", method = RequestMethod.GET)

	public ListResponse<UserRole> getByRoleId(@PathVariable("role-id") String roleId) {

		return userRoleService.findByRoleId(roleId);
	}

	/**
	 * Get user role by role id
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/userRole/user/{user-id}", method = RequestMethod.GET)

	public ListResponse<UserRole> getByUserId(@PathVariable("user-id") String userId) {

		return userRoleService.findByUserId(userId);
	}

	/**
	 * Delete user role by user id
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/userRole/user/{user-id}", method = RequestMethod.DELETE)

	public Response<String> deleteByUserId(@PathVariable("user-id") String userId) {

		return userRoleService.deleteByUserId(userId);
	}

	@RequestMapping(value = "/userRole/user/{user-id}/{role-id}", method = RequestMethod.DELETE)

	public Response<String> revokeRoleFromUser(@PathVariable("user-id") String userId,@PathVariable("role-id") String roleId) {

		return userRoleService.revokeRoleFromUser(userId,roleId);
	}

	
	/**
	 * Delete by Role Id
	 * 
	 * @param
	 * @return
	 */

	@RequestMapping(value = "/userRole/role/{role-id}", method = RequestMethod.DELETE)

	public Response<String> deleteByRoleId(@PathVariable("role-id") String roleId) {

		return userRoleService.deleteByRoleId(roleId);
	}

	/**
	 * Retrieve all user role
	 * 
	 * @return
	 */

	@RequestMapping(value = "/userRoles", method = RequestMethod.GET)
	public ListResponse<UserRole> getAllUserRoles() {

		return userRoleService.getAllUserRoles();
	}

}
