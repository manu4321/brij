package ca.brij.utils;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Repository;

import ca.brij.bean.notification.Notification;
import ca.brij.dao.notification.NotificationDao;

@Repository
@Configurable
public class NotificationSenderUtil {
	
	@Autowired
	private  NotificationDao notificationDao;
	
	@Autowired
	public NotificationSenderUtil(NotificationDao dao){
		this.notificationDao = dao;
	}
	
	public void makeNotification(Notification notification){
		notificationDao.save(notification);
	}
	public void makeNotification(String userID, String type, Integer targetID, String description){
		Notification notif = notificationDao.checkIfExist(userID, targetID, type);
		try{
			if(notif == null){
				notif = new Notification(userID, type, targetID, description);
			}else{
				notif.setCreationDate(Calendar.getInstance());
				notif.setDescription(description);
				notif.setReadFlag(false);
			}
			makeNotification(notif);
		}catch(Exception e){
			logger.error("Error making notification - continue" + e.getMessage());
		}

	}

	public  NotificationDao getNotificationDao() {
		return notificationDao;
	}
	
	public  void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String REQUEST_TYPE = "request";
	public static final String REQUEST_COMPLETE = "requestComplete";



}
