package ca.brij.dao.conversation;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import ca.brij.bean.conversation.Conversation;

public interface ConversationDao  extends JpaRepository<Conversation, Long> {
	
	public ArrayList<Conversation> getAllConversatons();
	
	public Conversation getByRequest(@Param("requestID") int requestID);
	
	
}
