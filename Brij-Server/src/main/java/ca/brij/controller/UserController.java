package ca.brij.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.maps.model.LatLng;

import ca.brij.bean.posting.Posting;
import ca.brij.bean.rating.Rating;
import ca.brij.bean.request.Request;
import ca.brij.bean.service.Service;
import ca.brij.bean.user.MyUserDetailsService;
import ca.brij.bean.user.User;
import ca.brij.bean.user.UserRole;
import ca.brij.dao.user.UserDao;
import ca.brij.utils.ApplicationProperties;
import ca.brij.utils.ConstantsUtil;
import ca.brij.utils.DaoHelper;
import ca.brij.utils.GeocodingHelper;
import ca.brij.utils.MergeBeanUtil;
import java.util.Properties;
import java.util.UUID;
import ca.brij.validation.Validator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
@RestController
public class UserController {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DaoHelper daoHelper;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private GeocodingHelper geoHelper;
	
	@Autowired
	private ApplicationProperties applicationProperties;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	@ResponseBody
	public String register(@RequestBody User userEntity) throws Exception {
		try {
			logger.info("Registering user: " + userEntity.getUsername());
			userEntity.setEnabled(true);
			String exceptions = Validator.userRegisterValid(userEntity);
			userEntity.setStatus(ConstantsUtil.INCOMPLETE);
			if (exceptions.equals("")) {
				String encryptedPassword = new BCryptPasswordEncoder().encode(userEntity.getPassword());
				userEntity.setPassword(encryptedPassword);
				UserRole userRole = new UserRole(userEntity, "ROLE_USER");
				userEntity.getUserRole().add(userRole);
				if (userDao.findByUserName(userEntity.getUsername()) != null) { 
					exceptions += "Username already exists";
					throw new Exception(ConstantsUtil.EXCEPTION_FLAG + exceptions);
				} else if (userDao.findByEmail(userEntity.getEmail()) != null){
					exceptions += "Email already exists";
					throw new Exception(ConstantsUtil.EXCEPTION_FLAG + exceptions);
				} else {
					userDao.save(userEntity);
					UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getUsername());
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
							encryptedPassword, userDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} else {
				throw new Exception(ConstantsUtil.EXCEPTION_FLAG + exceptions);
			}
		} catch (Exception e) {
			logger.error("Error registering user: " + userEntity.getUsername() + " message " + e.getMessage());
			throw e;
		}

		logger.info("Successfully registered user: " + userEntity.getUsername());
		return "Success";

	}
	
	@RequestMapping(value = "/user/rate", method = RequestMethod.POST)
	@ResponseBody
	public String rateUser(String username, @RequestBody Rating rate, Principal principal) throws Exception {
		try {
			User user = userDao.findByUserName(username);
			rate.setDate(Calendar.getInstance());
			rate.setUsername(principal.getName());
			user.getRatings().add(rate);
			userDao.save(user);
		} catch (Exception ex) {
			logger.error("error saving user(" + username + ") made by: " + principal.getName() + " message "
					+ ex.getMessage());
			throw ex;
		}
		logger.info("Successfully saved user(" +username + ") made by: " + principal.getName());
		return "Success";
	}

	@RequestMapping(value = "/user/save", method = RequestMethod.POST)
	@ResponseBody
	public String updateUser(@RequestBody User updatedUser, Principal principal) throws Exception {
		try {
			logger.info("Saving user: " + principal.getName());
			// password and enabled goes to null as we can't allow to be updated
			// through this request
			updatedUser.setPassword(null);
			updatedUser.setEnabled(null);
			updatedUser.setUserRole(null);
			updatedUser.setUsername(null);
			updatedUser.setStatus(null);
			updatedUser.setResetID(null);
			LatLng location = geoHelper.getLocationFromAddress(updatedUser.getAddress() + ", " + updatedUser.getCity() + ", " + updatedUser.getProvince());
			if (location != null) {
				updatedUser.setLatitude(location.lat);
				updatedUser.setLongitude(location.lng);
			} else {
				logger.info("Unable to save user: " + principal.getName() + " due to invalid address");
				throw new Exception(ConstantsUtil.EXCEPTION_FLAG + "Error locating address");
			}

			User originalUser = userDao.findByUserName(principal.getName());
			MergeBeanUtil.copyNonNullProperties(updatedUser, originalUser);
			// if the user didn't change anything in the status and the original
			// user is incomplete then make it active
			if (originalUser.getStatus().equals(ConstantsUtil.INCOMPLETE)) {
				originalUser.setStatus(ConstantsUtil.ACTIVE);
			}

			userDao.save(originalUser);
		} catch (Exception ex) {
			logger.error("Error saving user" + principal.getName() + "message: " + ex.getMessage());
			throw ex;
		}
		logger.info("Updating user " + principal.getName() + " was successful");
		return "Success";
	}

	@RequestMapping(value = "/admin/user/save", method = RequestMethod.POST)
	@ResponseBody
	public String updateUserByAdmin(@RequestBody User updatedUser, String username) throws Exception {
		try {
			logger.info("Saving user: " + username);
			// password and enabled goes to null as we can't allow to be updated
			// through this request
			updatedUser.setPassword(null);
			updatedUser.setEnabled(null);
			updatedUser.setUserRole(null);
			updatedUser.setUsername(null);
			LatLng location = geoHelper.getLocationFromAddress(
					updatedUser.getAddress() + ", " + updatedUser.getCity() + ", " + updatedUser.getProvince());
			if (location != null) {
				updatedUser.setLatitude(location.lat);
				updatedUser.setLongitude(location.lng);
			}

			User originalUser = userDao.findByUserName(username);
			MergeBeanUtil.copyNonNullProperties(updatedUser, originalUser);

			userDao.save(originalUser);
		} catch (Exception ex) {
			logger.error("Error saving user" + username + "message: " + ex.getMessage());
			throw ex;
		}
		logger.info("Updating user " + username + " was successful");
		return "Success";
	}

	/**
	 * GET /delete --> Delete the user having the passed id.
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/user/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public String delete(String username) throws Exception {
		try {
			logger.info("Deleting user" + username);
			User user = new User(username);
			userDao.delete(user);
		} catch (Exception ex) {
			logger.error("Registering user" + username + " message: " + ex.getMessage());
			throw ex;
		}
		logger.info("Deleting user" + username + " was successful");
		return "Success";
	}

	@RequestMapping("/user/current")
	@ResponseBody
	public Map<String, Object> getCurrentUser(Principal principal) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = null;
		Double avgRateAsPoster = 0.0;
		Integer noOfRatingsAsPoster = 0;
		Double avgRateByUser = 0.0;

		try {
			user = userDao.findByUserName(principal.getName());
			avgRateByUser = daoHelper.getUserDao().getAvgRating(principal.getName());
			noOfRatingsAsPoster = daoHelper.getPostingDao().getCountOfUser(principal.getName());
			avgRateAsPoster = daoHelper.getPostingDao().getAvgRatingByUser(principal.getName());
		} catch (Exception ex) {
			return null;
		}
		map.put("user", user);
		map.put("avgRateByUser", avgRateByUser == null? 0 : avgRateByUser);
		map.put("noOfRatingsByPoster", noOfRatingsAsPoster == null? 0 : noOfRatingsAsPoster);
		map.put("avgRateByPoster", avgRateAsPoster == null? 0 : avgRateAsPoster);

		return map;
	}

	@RequestMapping("/admin/user/current")
	@ResponseBody
	public User getUser(String username) {
		User user = null;
		try {
			user = userDao.findByUserName(username);
		} catch (Exception ex) {
			return null;
		}
		return user;
	}

	@RequestMapping("/admin/user/like")
	@ResponseBody
	public Map<String, Object> getLikeUsers(String username,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> userMap = new HashMap<String, Object>();

		ArrayList<User> users;
		int numberOfPages = 0;
		try {
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			users = userDao.findByUserLike(username, new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math.ceil((double) userDao.countUserLike(username) / (double) pageSize);
		} catch (Exception ex) {
			logger.info("Failed to retrieve all users " + ex.getMessage());
			return null;
		}
		userMap.put("users", users);
		userMap.put("numberOfPages", numberOfPages);
		userMap.put("currentPage", (pageNo + 1));
		return userMap;
	}

	/**
	 * Get all the users in the db
	 */
	@RequestMapping(value = "/admin/user/findAll", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllUser(@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		logger.info("Fiding All users");
		Map<String, Object> userMap = new HashMap<String, Object>();

		ArrayList<User> users;
		int numberOfPages = 0;
		try {
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			users = userDao.getAll(new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math.ceil((double) userDao.getCountAll() / (double) pageSize);

		} catch (Exception ex) {
			logger.info("Failed to retrieve all users " + ex.getMessage());
			return null;
		}
		userMap.put("users", users);
		userMap.put("numberOfPages", numberOfPages);
		userMap.put("currentPage", (pageNo + 1));
		return userMap;
	}


	/**
	 * forgotten password
	 */
	@RequestMapping(value = "/user/forgotpassword", method = RequestMethod.GET)
	@ResponseBody
	public void forgotPassword(String email) {
	 	final String username = "noreply.brij@gmail.com";
		final String password = "P@ssword1234";
			
		 Properties props = new Properties();
		    props.put("mail.smtp.auth", "true");
		    props.put("mail.smtp.starttls.enable", "true");
		    props.put("mail.smtp.host", "smtp.gmail.com");
		    props.put("mail.smtp.port", "587");

		    Session session = Session.getInstance(props,
		      new javax.mail.Authenticator() {
		        protected PasswordAuthentication getPasswordAuthentication() {
		            return new PasswordAuthentication(username, password);
		        }
		      });

		    try {
		    	String sub = "Brij App - Email Reset Link";
		    	String msg = "Please go to the following link to reset your password:\n\n";
		    	String sender = username;
		    	String to = email;
		    	final String FORGOT_PASSWORD_URL = "/resetpassword?resetid=";				
		    	
		    	String resetID = UUID.randomUUID().toString().replaceAll("-", "");
		    	
		    	User user = userDao.findByEmail(email);
		    	if (user != null) { 
			    	user.setResetID(resetID);
			    	userDao.save(user);
			    	
			    	msg += "<a href='" + applicationProperties.getServerURL() + FORGOT_PASSWORD_URL + resetID + "'>Reset Link</a>";
			    	
			        Message message = new MimeMessage(session);
			        message.setFrom(new InternetAddress(sender));
			        message.setRecipients(Message.RecipientType.TO,
			            InternetAddress.parse(to));
			        message.setContent(msg, "text/html; charset=utf-8");
			        message.setSubject(sub);
	
			        Transport.send(message);
		    	}
		    } catch (MessagingException e) {
		        logger.info(e.getMessage());
		    }
	}
	
	@RequestMapping(value = "/user/updateForgotPassword", method = RequestMethod.GET)
	@ResponseBody
	public String updateForgotPassword(String password1, String password2, String resetid) throws Exception {
		try {
			if (password1.equals(password2)) {
				User user = userDao.findUserByResetID(resetid);
								
				if (user != null) { 
					String encryptedPassword = new BCryptPasswordEncoder().encode(password1);
					user.setPassword(encryptedPassword);
					user.setResetID(null);
					userDao.save(user);
				} else {
					return null;
				}
			}
		} catch (Exception ex) {
			logger.error("Error updating password with resetID: " + resetid + " message: " + ex.getMessage());
			throw ex;
		}
		return "Success";
	}
	
	/**
	 * Update Password
	 */
	@RequestMapping(value = "/user/updatepassword", method = RequestMethod.GET)
	@ResponseBody
	public String updatePassword(Principal principal, String password1, String password2)  throws Exception{
		try {
			if (password1.equals(password2)) {
				User user = userDao.findByUserName(principal.getName());
								
				if (user != null) { 
					String encryptedPassword = new BCryptPasswordEncoder().encode(password1);
					user.setPassword(encryptedPassword);
					user.setResetID(null); //left in code because if user resets their password within account, they will no longer need this resetID to be active.
					userDao.save(user);
				} else {
					return "Unable to find user";
				}
			}
		} catch (Exception ex) {
			logger.error("Error updating password for: " + principal.getName() + " message: " + ex.getMessage());
			throw ex;
		}
		return "Success";
			
	}
}
