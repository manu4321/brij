package ca.brij.bean.conversation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ca.brij.bean.user.User;
import ca.brij.utils.ConstantsUtil;

@Entity
@NamedQueries({
	 	@NamedQuery(name = "Conversation.getAllConversatons", query = "from Conversation"),
		@NamedQuery(name = "Conversation.getById", query = "from Conversation where id = :id"), 
		@NamedQuery(name = "Conversation.getByTitle", query = "from Conversation where title = :title"),
		@NamedQuery(name = "Conversation.getByRequest", query = "from Conversation where requestID = :requestID")
})
@Table(name = "conversation", indexes = { @Index(name = "conversation_requestID", columnList = "requestID")})

public class Conversation {
	
	public Conversation(){
		
	}
	
	public Conversation(int requestID, String title){
		this.requestID = requestID;
		this.title = title;
		this.status = ConstantsUtil.ACTIVE;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private int id;
	
	@Column(name = "requestID", nullable = false, unique = true)
	private int requestID;
	
	@Column(name = "title")
	private String title;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@Column(name = "messages")
	private List<Message> messages = new ArrayList<Message>();
	
	@ElementCollection(fetch=FetchType.EAGER)
	@Column(name = "users")
	private List<String> users = new ArrayList<String>();
	
	@Column(name = "status")
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
	
	


}
