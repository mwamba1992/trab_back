package tz.go.mof.trab.service;



import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import tz.go.mof.trab.config.userextractor.LoggedUser;
import tz.go.mof.trab.dto.bill.RoleDto;
import tz.go.mof.trab.models.Role;
import tz.go.mof.trab.repositories.RoleRepository;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;
import tz.go.mof.trab.utils.ResponseCode;
import tz.go.mof.trab.utils.TrabHelper;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

	Response<Role> response = new Response<Role>();

	ListResponse<Role> responseList = new ListResponse<Role>();

	ListResponse<Role> listResponse = new ListResponse<Role>();

	@Autowired
	LoggedUser loggedUser;
	
	@Autowired
	RoleRepository roleRepository;

	@Override
	public Response<Role> saveRole(RoleDto roleDto) {

		try {
			Role role = new Role();

			TrabHelper.copyNonNullProperties(roleDto, role);

			TrabHelper.print(role);

			Optional<Role> optRoleName = roleRepository.findByName(roleDto.getName());

			if (optRoleName.isPresent() == true) {
				response.setData(null);
				response.setCode(ResponseCode.DUPLICATE);
				response.setDescription("Role name already exists");
				response.setStatus(false);
			} else {
				logger.info(" Registerring role with Details {}: ", role);
				
				if(loggedUser.getInfo()!=null) {
					  role.setCreatedBy(loggedUser.getInfo().getId());
					}		
				Role savedRole = roleRepository.save(role);
				response.setData(savedRole);
				response.setCode(ResponseCode.SUCCESS);
				response.setDescription("Data was successfull saved");
				response.setStatus(true);

			}

		} catch (Exception e) {
			logger.error("Failed to register role with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription(e.getMessage());
			response.setStatus(false);

		}

		return response;
	}

	@Override
	public Response<Role> findByName(String name) {
		try {

			Optional<Role> optRoleName = roleRepository.findByName(name);

			if (optRoleName.isPresent() == true) {
				response.setData(optRoleName.get());
				response.setCode(ResponseCode.SUCCESS);
				response.setDescription("Successfull");
				response.setStatus(true);
			} else {
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setDescription("Non record was found");
				response.setStatus(false);

			}

		} catch (Exception e) {
			logger.error("Failed to register role with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription(e.getMessage());
			response.setStatus(false);

		}

		return response;
	}

	@Override
	public Response<Role> deleteRole(String roleId) {
		try {

			Optional<Role> optRole = roleRepository.findById(roleId);

			if (optRole.isPresent() == true) {
				
				roleRepository.deleteById(roleId);
				response.setData(null);
				response.setCode(ResponseCode.SUCCESS);
				response.setDescription("Record was successfull deleted");
				response.setStatus(true);
			} else {
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setDescription("Non record was found");
				response.setStatus(false);

			}

		} catch (Exception e) {
			logger.error("Failed to delete role with error : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription(e.getMessage());
			response.setStatus(false);

		}

		return response;
	}

	@Override
	public ListResponse<Role> getAllRoles() {
		try {

			List<Role> availableRoles = roleRepository.findAll();

			if (availableRoles.size() > 0) {
				listResponse.setData(availableRoles);
				listResponse.setCode(ResponseCode.SUCCESS);
				listResponse.setDescription("List of roles");
				listResponse.setStatus(true);
			} else {
				listResponse.setData(null);
				listResponse.setCode(ResponseCode.NO_RECORD_FOUND);
				listResponse.setDescription("No record was found");
				listResponse.setStatus(false);
			}

		} catch (Exception e) {
			logger.error("Failed to retrieve all roles with error : {} ", e);
			listResponse.setData(null);
			listResponse.setCode(ResponseCode.FAILURE);
			listResponse.setDescription(e.getMessage());
			listResponse.setStatus(false);
		}

		return listResponse;

	}

	@Override
	public Response<Role> findById(String id) {
		try {

			Optional<Role> optRole = roleRepository.findById(id);

			if (optRole.isPresent() == true) {
				
				response.setData(optRole.get());
				response.setCode(ResponseCode.SUCCESS);
				response.setDescription("Successfull");
				response.setStatus(true);
			} else {
				response.setData(null);
				response.setCode(ResponseCode.NO_RECORD_FOUND);
				response.setDescription("Non record was found");
				response.setStatus(false);

			}

		} catch (Exception e) {
			logger.error("Failed to get role by id : {} ", e);
			response.setData(null);
			response.setCode(ResponseCode.FAILURE);
			response.setDescription(e.getMessage());
			response.setStatus(false);

		}

		return response;
	}


	@Override
	public Response<Role> updateRole(RoleDto roleDto, String roleId, BindingResult result) {
		// TODO Auto-generated method stub
		return null;
	}

}
