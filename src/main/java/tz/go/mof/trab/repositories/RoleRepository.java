package tz.go.mof.trab.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import tz.go.mof.trab.models.Role;
import java.util.Optional;


@Transactional
@CrossOrigin("*")
@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository  extends JpaRepository<Role, String>{
	
	@Modifying
	@Query(value = "delete from role where  role_id =:role_id", nativeQuery = true)
	public void deleteRole(@Param("role_id") Long role_id);
	
	
	@Query(value = "select * from role_permissions where role_id=:role_id AND permission_id=:permission_id", nativeQuery = true)
	public Object getActivePermissionRoles(@Param("role_id") Long role_id,@Param("permission_id") Long permission_id);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from role_permissions where role_id=:role_id AND permission_id=:permission_id", nativeQuery = true)
	public void deleteRolesPermision(@Param("role_id") Long role_id,@Param("permission_id") Long permission_id);
	
	
	@Modifying(clearAutomatically = true)
	@Query(value = "insert into role_permissions values(:role_id,:permission_id)", nativeQuery = true)
	public void saveRolesPermision(@Param("role_id") Long role_id,@Param("permission_id") Long permission_id);


	Optional<Role> findByName(String name);

	Optional<Role> findById(String id);
	

	
}
