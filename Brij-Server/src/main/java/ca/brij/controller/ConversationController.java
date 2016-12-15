package ca.brij.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.brij.bean.conversation.Conversation;
import ca.brij.bean.conversation.Message;
import ca.brij.bean.posting.Posting;
import ca.brij.bean.request.Request;
import ca.brij.bean.user.User;
import ca.brij.utils.DaoHelper;
import ca.brij.utils.NotificationSenderUtil;

@RestController
public class ConversationController {

	@Autowired
	public DaoHelper daoHelper;
	
	@Autowired
	public NotificationSenderUtil notifHelper;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/conversation/getByRequest", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getConversationByRequest(Integer id, Principal principal) throws Exception {
		Map<String, Object> conversationMap = new HashMap<String, Object>();

		try {
			Conversation conv = daoHelper.getConversationDao().getByRequest(id);
			if (conv == null) {
				// means is new. Make a new one with the information comming
				// from request
				Request req = daoHelper.getRequestDao().findById(id);

				// get post related to the request
				Posting post = daoHelper.getPostingDao().getPostingById(req.getPostID());

				// use the info to fill the conversation
				conv = new Conversation(id, "Conversation for " + post.getTitle());
				List<String> users = new ArrayList<String>();
				users.add(post.getUser().getUsername());
				users.add(req.getUserID());
				conv.setUsers(users);
				conv = daoHelper.getConversationDao().save(conv);

			}
			conversationMap.put("conversation", conv);
			conversationMap.put("currentUser", principal.getName());
		} catch (Exception e) {
			logger.error("Error occurred retrieving conversation by request " + e.getMessage());

		}

		return conversationMap;
	}

	@RequestMapping(value = "/conversation/saveMessage", method = RequestMethod.POST)
	@ResponseBody
	public String saveMessage(Integer id, @RequestBody Message message, Principal principal) throws Exception {

		try {
			message.setUsername(principal.getName());
			message.setDate(Calendar.getInstance());
			Conversation conv = daoHelper.getConversationDao().getByRequest(id);
			if(conv  == null){
				throw new Exception("Error -- conversation doesn't exist");
			}
			//add the new message
			conv.getMessages().add(message);
			daoHelper.getConversationDao().save(conv);
			String user = "";
			for(String u: conv.getUsers()){
				if(!u.equalsIgnoreCase(principal.getName())){
					user = u;
				}
			}
			notifHelper.makeNotification(user, "conversation", conv.getRequestID(), principal.getName() + " sent you a message");
			
		} catch (Exception e) {
			logger.error("Error occurred retrieving conversation by request " + e.getMessage());
			throw e;

		}

		return "success";
	}
}