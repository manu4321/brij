package ca.brij.bean.conversation;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Message {
	
	@Column(name = "username", nullable = false)
	private String username;
	
	@Column(name = "message", columnDefinition = "TEXT")
	private String message;
	
	@Column(name = "date")
	private Calendar date;
	
	public Message() {
	}
	public Message(String username, String message) {
		this.username = username;
		this.message = message;
		this.date = Calendar.getInstance();
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
}
