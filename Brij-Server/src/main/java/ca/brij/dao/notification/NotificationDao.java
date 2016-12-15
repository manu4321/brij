package ca.brij.dao.notification;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import ca.brij.bean.notification.Notification;

public interface NotificationDao extends JpaRepository<Notification, Long> {

	public ArrayList<Notification> getAllNotifications();

	public ArrayList<Notification> getNotificationByUserID(@Param("userID") String userID,  Pageable pageable);
	
	public Notification checkIfExist(@Param("userID") String userID, @Param("targetID") Integer targetID, @Param("type") String type);

	public Notification getNotificationById(@Param("id") int i);
	
	public Integer getCountOfUnread(@Param("userID") String userID);
	
	public Integer getCountOfNotificationUser(@Param("userID") String userID);

}
