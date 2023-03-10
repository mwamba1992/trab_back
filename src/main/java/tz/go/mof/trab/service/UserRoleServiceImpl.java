package tz.go.mof.trab.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.bill.RoleIdModel;
import tz.go.mof.trab.dto.bill.UserRoleDto;
import tz.go.mof.trab.models.Role;
import tz.go.mof.trab.models.SystemUser;
import tz.go.mof.trab.models.UserRole;
import tz.go.mof.trab.models.UserRoleId;
import tz.go.mof.trab.repositories.RoleRepository;
import tz.go.mof.trab.repositories.UserRepository;
import tz.go.mof.trab.repositories.UserRoleRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;


@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

	private static final Logger logger = LoggerFactory.getLogger(UserRoleServiceImpl.class);

	Response<UserRole> response = new Response<UserRole>();

	ListResponse<UserRole> responseList = new ListResponse<UserRole>();

	ListResponse<UserRole> listResponse = new ListResponse<UserRole>();

	Response<String> responseTxt = new Response<String>();

	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public Response<UserRole> saveUserRole(UserRoleDto userRoleDto) {

		logger.info("######" + userRoleDto.toString());

		try {
			Boolean flag = true;

			List<RoleIdModel> roleList = new ArrayList<RoleIdModel>();

			RoleIdModel roleIdModel = new RoleIdModel();
			roleIdModel.setRoleId(userRoleDto.getRoleId());

			roleList.add(roleIdModel);
			int successCounter = 0;
			int duplicateCounter = 0;
			String message = "";
			if (roleList.size() > 0) {

				for (RoleIdModel roleObj : roleList) {

					Optional<UserRole> exists = userRoleRepository
							.findById(new UserRoleId(userRoleDto.getUserId(), roleObj.getRoleId()));

					Optional<Role> roles = roleRepository.findById(roleObj.getRoleId());

					Optional<SystemUser> users = userRepository.findById(userRoleDto.getUserId());

					if (exists.isPresent() == true) {
						response.setData(null);
						response.setCode(ResponseCode.DUPLICATE);
						response.setDescription("User role already exists");
						response.setStatus(false);
						flag = false;
						duplicateCounter++;
						
					}

					if (!roles.isPresent()) {
						response.setData(null);
						response.setCode(ResponseCode.NO_RECORD_FOUND);
						response.setDescription("Role does not exists");
						response.setStatus(false);
						flag = false;
					}

					if (!users.isPresent()) {
						
						flag = false;
					}

					if (flag) {

						logger.info(" Registering role with Details {}: ", userRoleDto);


						UserRole userRole = new UserRole();
						userRole.setUserId(userRoleDto.getUserId());
						userRole.setRoleId(roleObj.getRoleId());
						UserRole savedUserRole = userRoleRepository.save(userRole);
						successCounter++;

					}
				}

				if (duplicateCounter > 0) {
					message = " and found " + duplicateCounter + " duplicate roles";
				}

				response.setCode(ResponseCode.SUCCESS);
				response.setDescription(successCounter + " roles was assigned to user " + message);
				response.setStatus(true);
				response.setData(null);
			} else {
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setDescription("No role was found");
				response.setStatus(false);
			}

		} catch (Exception e) {
			logger.error("Failed to register user role with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription(e.getMessage());
			response.setStatus(false);

		}

		return response;
	}

	@Override
	public ListResponse<UserRole> findByRoleId(String roleId) {
		try {

			List<UserRole> availableUserRole = userRoleRepository.findByRoleId(roleId);

			if (availableUserRole.size() > 0) {
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setDescription("User role");
				listResponse.setStatus(true);
				listResponse.setData(availableUserRole);

			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
				listResponse.setDescription("No record was found");

			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all user role by role id : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setDescription(null);
			listResponse.setStatus(false);
		}

		return listResponse;
	}

	@Override
	public ListResponse<UserRole> findByUserId(String userId) {
		try {

			List<UserRole> availableUserRole = userRoleRepository.findByUserId(userId);

			if (availableUserRole.size() > 0) {
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setDescription("User role");
				listResponse.setStatus(true);
				listResponse.setData(availableUserRole);

			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setStatus(false);
				listResponse.setDescription("No record was found");

			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all user role by user id : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setDescription(null);
			listResponse.setStatus(false);
		}

		return listResponse;
	}

	@Override
	public Response<String> deleteByUserId(String userId) {

		try {

			List<UserRole> availableUserRole = userRoleRepository.findByUserId(userId);

			if (availableUserRole.size() > 0) {

				userRoleRepository.deleteByUserId(userId);

				responseTxt.setCode(ResponseCode.SUCCESS);
				responseTxt.setDescription("User role was successfull deleted");
				responseTxt.setStatus(true);
				responseTxt.setData(null);

			} else {
				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.NO_RECORD_FOUND);
				responseTxt.setStatus(false);
				responseTxt.setDescription("No record was found");

			}

		} catch (Exception e) {
			logger.error("Failed to delete user role by user id : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(e.getMessage());
			responseTxt.setStatus(false);
		}

		return responseTxt;
	}

	@Override
	public Response<String> deleteByRoleId(String roleId) {

		try {

			List<UserRole> availableUserRole = userRoleRepository.findByRoleId(roleId);

			if (availableUserRole.size() > 0) {

				userRoleRepository.deleteByRoleId(roleId);
				responseTxt.setCode(ResponseCode.SUCCESS);
				responseTxt.setDescription("User role was successfull deleted");
				responseTxt.setStatus(true);
				responseTxt.setData(null);

			} else {
				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.NO_RECORD_FOUND);
				responseTxt.setStatus(false);
				responseTxt.setDescription("No record was found");

			}

		} catch (Exception e) {
			logger.error("Failed to delete user role by user id : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(e.getMessage());
			responseTxt.setStatus(false);
		}

		return responseTxt;
	}

	@Override
	public ListResponse<UserRole> getAllUserRoles() {
		try {

			List<UserRole> availableUserRole = userRoleRepository.findAll();

			if (availableUserRole.size() > 0) {
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setDescription(null);
				listResponse.setStatus(false);
				listResponse.setData(availableUserRole);

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
	public Response<String> revokeRoleFromUser(String userId, String roleId) {
		try {

			List<UserRole> availableUserRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId);

			if (availableUserRole.size() > 0) {

				userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
				responseTxt.setCode(ResponseCode.SUCCESS);
				responseTxt.setDescription("User role was successfull deleted");
				responseTxt.setStatus(true);
				responseTxt.setData(null);

			} else {
				responseTxt.setData(null);
				responseTxt.setCode(ResponseCode.NO_RECORD_FOUND);
				responseTxt.setStatus(false);
				responseTxt.setDescription("No record was found");

			}

		} catch (Exception e) {
			logger.error("Failed to delete user role by user id : {} ", e);
			responseTxt.setData(null);
			responseTxt.setCode(ResponseCode.FAILURE);
			responseTxt.setDescription(e.getMessage());
			responseTxt.setStatus(false);
		}

		return responseTxt;
	}

}
