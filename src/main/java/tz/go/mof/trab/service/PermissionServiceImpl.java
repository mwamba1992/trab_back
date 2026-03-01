package tz.go.mof.trab.service;


import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.bill.PermissionDto;
import tz.go.mof.trab.models.Permission;
import tz.go.mof.trab.repositories.PermissionRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

	private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

	@Autowired
	PermissionRepository permissionRepository;

	@Override
	public Response<Permission> savePermission(PermissionDto permissionDto) {
		Response<Permission> response = new Response<>();
		try {
			Permission permission = new Permission();

			TrabHelper.copyNonNullProperties(permissionDto, permission);

			TrabHelper.print(permission);

			Optional<Permission> optPermissionName = permissionRepository.findByName(permissionDto.getName());

			if (optPermissionName.isPresent()) {
				response.setData(null);
				response.setCode(ResponseCode.DUPLICATE);
				response.setDescription("Permission name already exists");
				response.setStatus(false);
			} else {
				logger.debug("Registering permission: {}", permission);
				Permission savedPermission = permissionRepository.save(permission);
				response.setData(savedPermission);
				response.setCode(ResponseCode.SUCCESS);
				response.setDescription("SUCCESS");
				response.setStatus(true);

			}

		} catch (Exception e) {
			logger.error("Failed to register permission", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription("FAILURE");
			response.setStatus(false);

		}

		return response;

	}

	@Override
	public Response<Permission> findByName(String name) {
		return null;
	}

	@Override
	public Response<Permission> findByPermissionId(String permissionId) {
		Response<Permission> response = new Response<>();
		try {

			Optional<Permission> permission = permissionRepository.findById(permissionId);

			if (permission.isPresent()) {
				response.setData(permission.get());
				response.setCode(ResponseCode.SUCCESS);
				response.setDescription("Permission Details");
				response.setStatus(true);
			} else {
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setDescription("Permission does not exist");
				response.setStatus(false);
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve permission details", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription("Failed to retrieve permission details");
			response.setStatus(false);
		}

		return response;
	}

	@Override
	public Response<Permission> updatePermission(PermissionDto permissionDto) {
		return null;
	}

	@Override
	public Response<Permission> deletePermission(String permissionId) {
		return null;
	}

	@Override
	public ListResponse<Permission> getAllPermissions() {
		ListResponse<Permission> listResponse = new ListResponse<>();
		try {

			List<Permission> availablePermissions = permissionRepository.findAll();

			if (!availablePermissions.isEmpty()) {
				listResponse.setData(availablePermissions);
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setStatus(true);
			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all permissions", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setStatus(false);
		}

		return listResponse;
	}

}
