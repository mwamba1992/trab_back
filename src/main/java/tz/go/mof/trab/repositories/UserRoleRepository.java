package tz.go.mof.trab.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.go.mof.trab.models.UserRole;
import tz.go.mof.trab.models.UserRoleId;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

	List<UserRole> findByRoleId(String roleId);

	List<UserRole> findByUserId(String userId);

	void deleteByUserId(String userId);

	void deleteByRoleId(String roleId);
	
	void deleteByUserIdAndRoleId(String userId,String roleId);
	   
	List<UserRole> findByUserIdAndRoleId(String userId,String roleId);

}
