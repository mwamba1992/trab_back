package tz.go.mof.trab.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tz.go.mof.trab.models.SystemUser;

@Repository
public interface UserRepository extends JpaRepository<SystemUser, String> {

	Optional<SystemUser> findByUsername(String username);
	
	Optional<SystemUser> findByEmail(String email);
	
	Optional<SystemUser> findById(String id);

	List<SystemUser> findByCheckNumber(String checkNumber);
	
	List<SystemUser> findByEnabled(Boolean enabled);

	@Modifying
	@Query(value = "UPDATE trr_users SET account_non_locked=1,login_attempt=0,locked_at=null WHERE account_non_locked=0 AND DATEDIFF(minute,locked_at,?1)>15", 
	  nativeQuery = true)
	int unLockUserAccount(LocalDateTime currentTime);
	
	
	@Modifying
	@Query(value = "UPDATE trr_users SET account_non_expired=0,new_account=1,enabled=0 WHERE DATEDIFF(day,last_login_at,?1)>90", 
	  nativeQuery = true)
	int disableUserAccount(LocalDateTime currentTime);  
	
	
	
}
