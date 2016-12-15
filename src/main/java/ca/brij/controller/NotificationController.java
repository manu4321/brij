package ca.brij.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.brij.bean.notification.Notification;
import ca.brij.bean.posting.Posting;
import ca.brij.dao.notification.NotificationDao;
import ca.brij.utils.MergeBeanUtil;

@RestController
public class NotificationController {

	
	@Autowired
	private NotificationDao notificationDao;
	
	
	@RequestMapping(value = "/notification/edit", method = RequestMethod.POST)
	@ResponseBody
	public String updateNotification(@RequestBody Notification notification) throws Exception {
		try {
			logger.info("saving notification " + notification.getId());
			Integer id = notification.getId();
			if(id == null){
				throw new Exception("Notification lacks Ids");
			}
			Notification oldNotification = notificationDao.getNotificationById(id);
			if(oldNotification == null){
				oldNotification = notification;
			}else{
				MergeBeanUtil.copyNonNullProperties(notification, oldNotification);
			}
			notificationDao.save(oldNotification);
		}catch(Exception e){
			logger.error("Error saving notification");
			throw e;
		}
		return "Success";
	}
	
	/**
	 * Find By User
	 * @throws Exception 
	 */
	@RequestMapping(value = "/admin/notification/findAll", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<Notification> getAllNotifications() throws Exception {
		ArrayList<Notification> notifications = null;
		
		try {
			logger.info("retrieving all posts");
			notifications = notificationDao.getAllNotifications();
		} catch (Exception ex) {
			logger.error("Error retrieving all notifications");
			throw ex;
		}
		logger.info("successfully retrieved notifications");
		return notifications;
	}
	
	/**
	 * Find By User
	 * @throws Exception 
	 */
	@RequestMapping(value = "/notification/findByUser", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getNotificationForUser(Principal principal, @RequestParam(value = "pageNo", required=false) Integer pageNo, @RequestParam(value = "pageSize", required=false) Integer pageSize) throws Exception {
		Map <String, Object> notificationMap = new HashMap<String, Object>();
		ArrayList<Notification> notifications = null;
		Integer noOfUnRead = 0;
		int numberOfPages = 0;

		try {
			if(pageNo == null){
				pageNo = 0;
			}
			if(pageSize == null){
				pageSize = 10;
			}
			logger.info("retrieving all notifications made by: " + principal.getName());
			notifications = notificationDao.getNotificationByUserID(principal.getName(), new PageRequest(pageNo, pageSize));
			noOfUnRead = notificationDao.getCountOfUnread(principal.getName());
			numberOfPages = (int)Math.ceil((double)notificationDao.getCountOfNotificationUser(principal.getName()) / (double)pageSize);

		} catch (Exception ex) {
			logger.error("Error retrieving all notifications made by " + principal.getName());
			throw ex;
		}
		notificationMap.put("notifications", notifications);
		notificationMap.put("noOfUnRead", noOfUnRead);
		notificationMap.put("numberOfPages", numberOfPages);
		notificationMap.put("currentPage", (pageNo + 1));
		logger.info("successfully retrieved notifications made by: " + principal.getName());
		return notificationMap;
	}
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    

}
