package tz.go.mof.trab.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.models.Permission;
import tz.go.mof.trab.models.Role;
import tz.go.mof.trab.models.SystemUser;
import tz.go.mof.trab.repositories.SystemUserRepository;




@Service
public class CustomUserDetailsService implements UserDetailsService {

	private Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Autowired
	private SystemUserRepository userAccountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    SystemUser userAccount = null;
		try {

			userAccount = userAccountRepository.findByUsername(username);

			// Added

			if (!userAccount.isAccountNonLocked()) {
				logger.debug("User account is locked");

				throw new LockedException("User account is locked");
			}

			if (!userAccount.isEnabled()) {
				logger.debug("User account is disabled");

				throw new DisabledException("User is disabled");
			}

			if (!userAccount.isAccountNonExpired()) {
				logger.debug("User account is expired");

				throw new AccountExpiredException("User account has expired");
			}

			// Ends

			Collection<? extends GrantedAuthority> authorities = getAuthorities(userAccount.getRolesList());

			userAccount.setAuthorities(authorities);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return userAccount;

	}

	private Collection<? extends GrantedAuthority> getAuthorities(List<Role> rolesList) {

		List<GrantedAuthority> authorities = new ArrayList<>();

		for (Role role : rolesList) {
			System.out.println("**************** ROLES *********************" + role.getPermissions());
			role.getPermissions().forEach(System.out::println);
			for (Permission permission : role.getPermissions()) {
				authorities.add(new SimpleGrantedAuthority(permission.getDisplayName()));
			}
		}
		return authorities;
	}

}
