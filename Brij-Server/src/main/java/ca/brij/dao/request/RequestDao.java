package ca.brij.dao.request;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import ca.brij.bean.request.Request;

@Transactional
public interface RequestDao  extends JpaRepository<Request, Long> {

	public ArrayList<Request> findAll();
	
	public Request findById(@Param("requestID") Integer integer);
	
	public ArrayList<Request> findByUser(@Param("userID") String userID, Pageable pageable);
	
	public ArrayList<Request> findByPost(@Param("postID") int postID,  Pageable pageable);
	
	public ArrayList<Request> findByUserAndPost(@Param("userID") String userID, @Param("postID") int postID);

	public int getCountForUser(@Param("userID") String userID);

	public int getCountForPost(@Param("postID") int postID);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Request set status = :status WHERE postID = :postID")
	void changeState(@Param("status") String status, @Param("postID") int postID);
		
}
