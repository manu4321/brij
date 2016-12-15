package ca.brij.bean.ticket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ca.brij.bean.conversation.Message;

@Entity
@NamedQueries({
	 	@NamedQuery(name = "Ticket.getAllTicket", query = "from Ticket WHERE status <> 'closed' ORDER BY creationDate DESC"),
	 	@NamedQuery(name = "Ticket.countAllTicket", query = "SELECT count(*) from Ticket WHERE status <> 'closed'"),
	 	@NamedQuery(name = "Ticket.getTicketByID", query = "from Ticket WHERE id = :id "),
	 	@NamedQuery(name = "Ticket.getAllTicketAdmin", query = "from Ticket ORDER BY creationDate DESC"),
	 	@NamedQuery(name = "Ticket.countAllTicketAdmin", query = "SELECT count(*) from Ticket"),
	 	@NamedQuery(name = "Ticket.getTicketByUser", query = "from Ticket WHERE userID = :userID  ORDER BY creationDate DESC"),
	 	@NamedQuery(name = "Ticket.countTicketByUser", query = "SELECT count(*) from Ticket WHERE userID = :userID "),
		@NamedQuery(name = "Ticket.getTicketByTypeAdmin", query = "from Ticket where type = :type"),
		@NamedQuery(name = "Ticket.countTicketByTypeAdmin", query = "SELECT count(*) from Ticket where type = :type"),
		@NamedQuery(name = "Ticket.getTicketByType", query = "from Ticket where type = :type AND userID = :userID"),
		@NamedQuery(name = "Ticket.countTicketByType", query = "SELECT count(*) from Ticket where type = :type AND userID = :userID")


})
public class Ticket {
	
	
	public Ticket() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private int id;
	
	@Column(name = "userID")
	private String userID;
	
	@Column(name = "type", length = 100)
	private String type;
	
	@Column(name = "comment", columnDefinition ="TEXT")
	private String comment;
	
	@Column(name = "creationDate")
	private Calendar creationDate;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@Column(name = "messages")
	private List<Message> messages = new ArrayList<Message>();
	
	@Column(name = "status")
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
}
