package ca.brij.bean.request;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({ 
		@NamedQuery(name = "Request.findAll", query = "FROM Request"),
		@NamedQuery(name = "Request.findById", query = "FROM Request WHERE requestID = :requestID"),
		@NamedQuery(name = "Request.findByUser", query = "FROM Request WHERE userID = :userID"),
		@NamedQuery(name = "Request.findByPost", query = "FROM Request WHERE postID = :postID"),
		@NamedQuery(name = "Request.findByUserAndPost", query = "FROM Request WHERE userID = :userID AND postID = :postID AND status IN('pending', 'in_progress')"),
		@NamedQuery(name = "Request.getCountForUser", query = "SELECT COUNT(*) FROM Request WHERE userID = :userID"),
		@NamedQuery(name = "Request.getCountForPost", query = "SELECT COUNT(*) FROM Request WHERE postID = :postID")
})
@Table(name = "request", indexes = { @Index(name = "request_userInd", columnList = "userID"),
		@Index(name = "request_postInd", columnList = "postID")})
public class Request implements Serializable{


	private static final long serialVersionUID = -2762931721055951008L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "requestID", nullable = false, unique = true)
	private Integer requestID;

	@Column(name = "userID", nullable = false)
	private String userID;
	
	@Column(name = "postID", nullable = false)
	private Integer postID;
	
	@Column(name = "creationDate", nullable = false)
	private Calendar creationDate;
	
	@Column(name = "notes", columnDefinition = "TEXT")
	private String notes;
	
	@Column(name = "status")
	private String status;

	public Integer getRequestID() {
		return requestID;
	}

	public void setRequestID(Integer requestID) {
		this.requestID = requestID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Integer getPostID() {
		return postID;
	}

	public void setPostID(Integer postID) {
		this.postID = postID;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
