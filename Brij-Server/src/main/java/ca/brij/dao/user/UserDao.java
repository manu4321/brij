package ca.brij.dao.user;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import ca.brij.bean.user.User;

@Transactional
public interface UserDao extends JpaRepository<User, Long> {

	public ArrayList<User> getAll(Pageable pageable);
	
	public Integer getCountAll();

	public User findByUserName(@Param("username") String username);

	public ArrayList<User> findByUserLike(@Param("username") String username, Pageable pageable);
	
	public Integer countUserLike(@Param("username") String username );

	public User findUser(@Param("username") String username, @Param("password") String password);

	public User findByEmail(@Param("email") String email);

	public User findUserByResetID(@Param("resetID") String resetID);
	
	public Double getAvgRating(@Param("username") String username );

}
