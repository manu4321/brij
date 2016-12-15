package ca.brij.utils;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import ca.brij.dao.conversation.ConversationDao;
import ca.brij.dao.notification.NotificationDao;
import ca.brij.dao.posting.PostingDao;
import ca.brij.dao.request.RequestDao;
import ca.brij.dao.service.ServiceDao;
import ca.brij.dao.ticket.TicketDao;
import ca.brij.dao.user.UserDao;

@Component
public class DaoHelper {
	
	public DaoHelper(){
		
	}
	
	@Resource
	private PostingDao postingDao;
	
	@Resource
	private ServiceDao serviceDao;
	
	@Resource
	private RequestDao requestDao;
	
	@Resource
	private UserDao userDao;
	
	@Resource 
	private NotificationDao notificationDao;
	
	@Resource
	private ConversationDao conversationDao;
	
	@Resource
	private TicketDao ticketDao;

	public PostingDao getPostingDao() {
		return postingDao;
	}

	public void setPostingDao(PostingDao postingDao) {
		this.postingDao = postingDao;
	}

	public ServiceDao getServiceDao() {
		return serviceDao;
	}

	public void setServiceDao(ServiceDao serviceDao) {
		this.serviceDao = serviceDao;
	}

	public RequestDao getRequestDao() {
		return requestDao;
	}

	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public NotificationDao getNotificationDao() {
		return notificationDao;
	}

	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public ConversationDao getConversationDao() {
		return conversationDao;
	}

	public void setConversationDao(ConversationDao conversationDao) {
		this.conversationDao = conversationDao;
	}

	public TicketDao getTicketDao() {
		return ticketDao;
	}

	public void setTicketDao(TicketDao ticketDao) {
		this.ticketDao = ticketDao;
	}
	
	
}
