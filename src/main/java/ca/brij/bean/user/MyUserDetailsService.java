package ca.brij.bean.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.brij.dao.user.UserDao;
import ca.brij.utils.ConstantsUtil;

@Service("authService")
public class MyUserDetailsService implements UserDetailsService{

	@Autowired
	private UserDao userDao;
	

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ca.brij.bean.user.User user = userDao.findByUserName(username);
		if(user == null){
			throw new UsernameNotFoundException("User Not found");
		}else if(user.getStatus().equals(ConstantsUtil.CLOSED)){
			throw new UsernameNotFoundException("This account has been closed");
		}
		//TODO check for null
		List<GrantedAuthority> authorities = buildUserAuthority(user.getUserRole());
		return buildUserForAuthentication(user, authorities);
	}
	
	private User buildUserForAuthentication(ca.brij.bean.user.User user, List<GrantedAuthority> authorities){
		return new User(user.getUsername(), user.getPassword(), user.getEnabled(), true, true, true, authorities);
	}
	
	private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles){
		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
		
		//build users authorities
		for(UserRole userRole: userRoles){
			setAuths.add(new SimpleGrantedAuthority(userRole.getRole()));
		}
		List<GrantedAuthority> result = new ArrayList<GrantedAuthority>(setAuths);
		return result;
	}

}
