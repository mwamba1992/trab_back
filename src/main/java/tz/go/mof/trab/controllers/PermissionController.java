package tz.go.mof.trab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tz.go.mof.trab.models.Permission;
import tz.go.mof.trab.service.PermissionService;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

@RestController
@RequestMapping(value = "/api")

public class PermissionController {

	@Autowired
	private PermissionService permissionService;

	@RequestMapping(value = "/permissions", method = RequestMethod.GET)
	public ListResponse<Permission> getAllPermissions() {

		return permissionService.getAllPermissions();
	}


	@RequestMapping(value = "/permission/{permission-id}", method = RequestMethod.GET)
	public Response<Permission> getPermissionById(@PathVariable("permission-id") String permissionId) {

		return permissionService.findByPermissionId(permissionId);
	}

}
