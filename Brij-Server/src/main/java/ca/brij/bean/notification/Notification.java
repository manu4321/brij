package ca.brij.bean.notification;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({ @NamedQuery(name = "Notification.getAllNotifications", query = "from Notification"),
		@NamedQuery(name = "Notification.getNotificationById", query = "from Notification where id = :id ORDER BY creationDate DESC"),
		@NamedQuery(name = "Notification.checkIfExist", query = "from Notification where userID = :userID AND targetID = :targetID AND type = :type ORDER BY creationDate DESC"),
		@NamedQuery(name = "Notification.getNotificationByUserID", query = "from Notification where userID = :userID ORDER BY creationDate DESC"),
		@NamedQuery(name = "Notification.getCountOfUnread", query = "SELECT count(*) from Notification where userID = :userID AND readFlag = 0"),
		@NamedQuery(name = "Notification.getCountOfNotificationUser", query = "SELECT count(*) from Notification where userID = :userID")
})
@Table(name = "Notification", indexes = { @Index(name = "notification_userIDInd", columnList = "userID")})
public class Notification implements Serializable {

	
	public Notification() {

	}
	
	public Notification(String userID, String type, Integer targetID, String description){
		this.userID = userID;
		this.type = type;
		this.description = description;
		this.targetID = targetID;
		this.readFlag = false;
		this.creationDate = Calendar.getInstance();
	}


	private static final long serialVersionUID = -1961371020363945182L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Integer id;
	
	@Column(name = "userID", nullable = false)
	private String userID;
	
	@Column(name = "type", nullable = false)
	private String type;
	
	@Column(name = "targetID", nullable = false)
	private Integer targetID;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "readFlag")
	private Boolean readFlag;
	
	@Column(name = "creationDate")
	private Calendar creationDate;
	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Integer getTargetID() {
		return targetID;
	}
	public void setTargetID(Integer targetID) {
		this.targetID = targetID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(Boolean readFlag) {
		this.readFlag = readFlag;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
	
	
}
