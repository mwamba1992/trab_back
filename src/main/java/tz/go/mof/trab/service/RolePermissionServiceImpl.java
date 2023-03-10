package tz.go.mof.trab.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.permission.PermissionIdModel;
import tz.go.mof.trab.dto.permission.RolePermissionsDto;
import tz.go.mof.trab.models.RolePermissions;
import tz.go.mof.trab.models.RolePermissionsId;
import tz.go.mof.trab.repositories.RolePermissionRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;


@Service
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

	private static final Logger logger = LoggerFactory.getLogger(RolePermissionServiceImpl.class);

	Response<RolePermissions> response = new Response<RolePermissions>();

	ListResponse<RolePermissions> responseList = new ListResponse<RolePermissions>();

	ListResponse<RolePermissions> listResponse = new ListResponse<RolePermissions>();

	Response<String> responseTxt = new Response<String>();

	@Autowired
	RolePermissionRepository rolePermissionRepository;

	@Override
	public Response<RolePermissions> saveRolePermission(RolePermissionsDto rolePermissionDto) {

		try {

			List<PermissionIdModel> permissions = rolePermissionDto.getPermissionList();
			int successCounter = 0;
			int duplicateCounter = 0;
			String message = "";
			if (permissions.size() > 0) {

				for (PermissionIdModel permissionObj : permissions) {

					String permissionId = permissionObj.getPermissionId();
					Optional<RolePermissions> exists = rolePermissionRepository
							.findById(new RolePermissionsId(rolePermissionDto.getRoleId(), permissionId));

					if (exists.isPresent()) {
						duplicateCounter++;
					} else {
						logger.info(" New permission assigned to role {}: ", permissionId);

						RolePermissions rolePermissions = new RolePermissions();
						rolePermissions.setPermissionId(permissionId);
						rolePermissions.setRoleId(rolePermissionDto.getRoleId());
						rolePermissionRepository.save(rolePermissions);
						successCounter++;
					}
				}

				if (duplicateCounter > 0) {
					message = " and found " + duplicateCounter + " duplicate permission";
				}

				response.setCode(ResponseCode.SUCCESS);
				response.setDescription(successCounter + " Permissions was assigned to role " + message);
				response.setStatus(true);
				response.setData(null);
			}

		} catch (Exception e) {
			logger.error("Failed to register role permission with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription(null);
			response.setStatus(false);

		}

		return response;
	}

	@Override
	public ListResponse<RolePermissions> findByRoleId(String roleId) {

		try {

			List<RolePermissions> availableRolePermissions = rolePermissionRepository.findByRoleId(roleId);

			if (availableRolePermissions.size() > 0) {
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setDescription(null);
				listResponse.setStatus(false);
				listResponse.setData(availableRolePermissions);

			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
				listResponse.setDescription(null);

			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all role permissions with error : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setDescription(null);
			listResponse.setStatus(false);
		}

		return listResponse;

	}

	@Override
	public ListResponse<RolePermissions> findByPermissionId(String permissionId) {
		try {

			List<RolePermissions> availableRolePermissions = rolePermissionRepository.findByPermissionId(permissionId);

			if (availableRolePermissions.size() > 0) {
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setDescription(null);
				listResponse.setStatus(false);
				listResponse.setData(availableRolePermissions);

			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
				listResponse.setDescription(null);

			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all role permissions with error : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setDescription(null);
			listResponse.setStatus(false);
		}

		return listResponse;
	}

	@Override
	public ListResponse<RolePermissions> getAllRolePermissions() {

		try {

			List<RolePermissions> availableRolePermissions = rolePermissionRepository.findAll();

			if (availableRolePermissions.size() > 0) {
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setDescription(null);
				listResponse.setStatus(false);
				listResponse.setData(availableRolePermissions);

			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
				listResponse.setDescription(null);

			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all role permissions with error : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setDescription(null);
			listResponse.setStatus(false);
		}

		return listResponse;

	}

	@Override
	public Response<String> deleteByPermissionId(String permissionId) {

		try {

			List<RolePermissions> permissions = rolePermissionRepository.findByPermissionId(permissionId);

			if (permissions.size() > 0) {

				logger.info(" Delete all permissions with id {}: ", permissionId);

				rolePermissionRepository.deleteByPermissionId(permissionId);
				responseTxt.setCode(ResponseCode.SUCCESS);
				responseTxt.setDescription("Data was successfully deleted");
				responseTxt.setStatus(true);
				responseTxt.setData(null);
			} else {

				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.NO_RECORD_FOUND);
				responseTxt.setDescription("No record was found");
				responseTxt.setStatus(false);

			}

		} catch (Exception e) {
			logger.error("Failed to delete role permission : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(null);
			responseTxt.setStatus(false);

		}

		return responseTxt;
	}

	@Override
	public Response<String> deleteByRoleId(String roleId) {
		try {

			List<RolePermissions> permissions = rolePermissionRepository.findByRoleId(roleId);

			if (permissions.size() > 0) {

				logger.info(" Delete all permissions with id {}: ", roleId);

				rolePermissionRepository.deleteByRoleId(roleId);
				responseTxt.setCode(ResponseCode.SUCCESS);
				responseTxt.setDescription("Data was successfull deleted");
				responseTxt.setStatus(true);
				responseTxt.setData(null);
			} else {

				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.NO_RECORD_FOUND);
				responseTxt.setDescription("No record was found");
				responseTxt.setStatus(false);

			}

		} catch (Exception e) {
			logger.error("Failed to delete role permission : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(null);
			responseTxt.setStatus(false);

		}

		return responseTxt;
	}

	@Override
	public Response<String> deleteRolePermission(RolePermissionsDto rolePermissionDto) {

		try {

			List<PermissionIdModel> permissions = rolePermissionDto.getPermissionList();
			int deletedCounter = 0;
			if (permissions.size() > 0) {
				for (PermissionIdModel permissionObj : permissions) {

					String permissionId = permissionObj.getPermissionId();

					RolePermissionsId id = new RolePermissionsId(rolePermissionDto.getRoleId(), permissionId);

					Optional<RolePermissions> exists = rolePermissionRepository.findById(id);

					if (exists.isPresent()) {
						rolePermissionRepository.deleteById(id);
						deletedCounter++;
					}
				}
				if (deletedCounter > 0) {
					responseTxt.setData(null);
					responseTxt.setCode(ResponseCode.SUCCESS);
					responseTxt.setDescription(deletedCounter + " role permission were deleted");
					responseTxt.setStatus(false);
				} else {
					responseTxt.setData(null);
					responseTxt.setCode(ResponseCode.FAILURE);
					responseTxt.setDescription("No role permission were deleted");
					responseTxt.setStatus(false);
				}

			} else {
				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.FAILURE);
				responseTxt.setDescription("Sorry no permission were found");
				responseTxt.setStatus(false);
			}

		} catch (Exception e) {
			logger.error("Failed to delete role permission : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(e.getMessage());
			responseTxt.setStatus(false);

		}

		return responseTxt;

	}

}
