package tz.go.mof.trab.service;


import tz.go.mof.trab.dto.bill.UserRoleDto;
import tz.go.mof.trab.models.UserRole;
import tz.go.mof.trab.utils.ListResponse;
import tz.go.mof.trab.utils.Response;

public interface UserRoleService {

		public Response<UserRole> saveUserRole(UserRoleDto userRoleDto);
		
		public ListResponse<UserRole> findByRoleId(String roleId);
		
		public ListResponse<UserRole> findByUserId(String userId);

		public Response<String> deleteByUserId(String userId);
		
		public Response<String> revokeRoleFromUser(String userId,String roleId);
		
		public Response<String> deleteByRoleId(String roleId);

		public ListResponse<UserRole> getAllUserRoles();	
	
}
