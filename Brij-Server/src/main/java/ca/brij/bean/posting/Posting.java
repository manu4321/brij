package ca.brij.bean.posting;

import java.io.Serializable;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import ca.brij.bean.rating.Rating;
import ca.brij.bean.user.User;


@Entity
@NamedQueries({
		@NamedQuery(name = "Posting.getAllPostings", query = "from Posting WHERE status <> 'closed' ORDER BY creationDate DESC "),
		@NamedQuery(name = "Posting.getAllPostingsAdmin", query = "from Posting ORDER BY creationDate DESC "),
		@NamedQuery(name = "Posting.getCountOfAllAdmin", query = "SELECT count(*) from Posting ORDER BY creationDate DESC "),
		@NamedQuery(name = "Posting.getPostsByLocation", query = "SELECT Posting FROM Posting Posting  WHERE ( 6371 * acos( cos( radians(:latitude) ) * cos( radians( user.latitude ) ) * cos( radians( user.longitude ) - radians(:longitude) ) + sin( radians(:latitude) ) * sin( radians( user.latitude ) ) ) ) < :distance AND status <> 'closed' ORDER BY creationDate DESC"),
		@NamedQuery(name = "Posting.getPostingById", query = "from Posting where id = :id AND status <> 'closed'"),
		@NamedQuery(name = "Posting.getPostingByIdAdmin", query = "from Posting where id = :id"),
		@NamedQuery(name = "Posting.getAvgRating", query = "SELECT avg(r.value) from Posting p JOIN p.ratings r WHERE id = :id"),
		@NamedQuery(name = "Posting.getAvgRatingByUser", query = "SELECT avg(r.value) from Posting p JOIN p.ratings r WHERE p.user.username = :userID"),
		@NamedQuery(name = "Posting.getPostingsByServID", query = "from Posting where servID = :servID"),
		@NamedQuery(name = "Posting.getPostingsLikeTitleAdmin", query = "from Posting where LOWER(title) LIKE LOWER('%' || :title || '%')"),
		@NamedQuery(name = "Posting.getPostingsLikeTitle", query = "from Posting where LOWER(title) LIKE LOWER('%' || :title || '%')"),
		@NamedQuery(name = "Posting.getPostsByLocationLikeTitle", query = "SELECT Posting FROM Posting Posting  WHERE LOWER(title) LIKE LOWER('%' || :title || '%') AND (6371 * acos( cos( radians(:latitude) ) * cos( radians( user.latitude ) ) * cos( radians( user.longitude ) - radians(:longitude) ) + sin( radians(:latitude) ) * sin( radians( user.latitude ) ) ) ) < :distance AND status <> 'closed' ORDER BY creationDate DESC"),
		@NamedQuery(name = "Posting.getCountOfPostLikeAdmin", query = "SELECT count(*) from Posting where LOWER(title) LIKE LOWER('%' || :title || '%')"),
		@NamedQuery(name = "Posting.getPostingsByUserID", query = "from Posting where user.username = :userID AND status <> 'closed' ORDER BY creationDate DESC"),
		@NamedQuery(name = "Posting.getCountOfAll", query = "SELECT count(*) from Posting WHERE status <> 'closed' ORDER BY creationDate DESC"),
		@NamedQuery(name = "Posting.getCountOfUser", query = "SELECT count(*) from Posting  where user.username = :userID AND  status <> 'closed'") })
@Table(name = "posting", indexes = { @Index(name = "posting_nameInd", columnList = "title") })
@DynamicUpdate
public class Posting implements Serializable {

	private static final long serialVersionUID = 8816634543519363815L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Integer id;

	@Column(name = "title", nullable = false)
	private String title;

	// get this from the form (dropdown has values);
	@Column(name = "servID", nullable = false)
	private Integer servID;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "username")
	private User user;

	@Column(name = "details", columnDefinition = "TEXT")
	private String details;

	@Column(name = "isPost")
	private Boolean isPost;

	@Column(name = "creationDate")
	private Calendar creationDate;

	@Column(name = "status")
	private String status;

	@ElementCollection(fetch=FetchType.EAGER)
	@Column(name = "ratings")
	private List<Rating> ratings = new ArrayList<Rating>();
	
	
	public Posting() {
	}

	public Posting(Integer id, String name) {
		this.id = id;
		this.title = name;
		this.creationDate = Calendar.getInstance();
	}

	public Posting(Integer id, String title, User user, Integer servID) {
		this.id = id;
		this.title = title;
		this.user = user;
		this.servID = servID;
		this.creationDate = Calendar.getInstance();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getServID() {
		return servID;
	}

	public void setServID(Integer servID) {
		this.servID = servID;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Boolean getIsPost() {
		return isPost;
	}

	public void setIsPost(Boolean isPost) {
		this.isPost = isPost;
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

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}
	

}