package ca.brij.controller;

import java.io.Console;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.brij.bean.posting.Posting;
import ca.brij.bean.rating.Rating;
import ca.brij.bean.request.Request;
import ca.brij.bean.service.Service;
import ca.brij.bean.user.User;
import ca.brij.utils.ConstantsUtil;
import ca.brij.utils.DaoHelper;
import ca.brij.utils.MergeBeanUtil;

@RestController
public class PostingController {

	@Autowired
	private DaoHelper daoHelper;

	@RequestMapping(value = "/posting/save", method = RequestMethod.POST)
	@ResponseBody
	public String updatePost(@Validated @RequestBody Posting post, Principal principal) throws Exception {
		try {
			logger.info("saving post(" + post.getTitle() + ") made by: " + principal.getName());
			User user = daoHelper.getUserDao().findByUserName(principal.getName());
			if(user == null || user.getStatus().equals(ConstantsUtil.INCOMPLETE)){
				throw new Exception(ConstantsUtil.EXCEPTION_FLAG + " User needs to complete account before making a new post");
			}
			post.setUser(user);
			Posting origPost = null;
			if (post.getId() != null) {
				origPost = daoHelper.getPostingDao().getPostingById(post.getId());

			}
			// means is new
			if (origPost == null) {
				origPost = post;
				origPost.setCreationDate(Calendar.getInstance());
				origPost.setStatus(ConstantsUtil.ACTIVE);
			} else {
				// update current one by saving only the changed values (not
				// null)
				post.setStatus(null);
				MergeBeanUtil.copyNonNullProperties(post, origPost);

			}
			daoHelper.getPostingDao().save(origPost);
		} catch (Exception ex) {
			logger.error("error saving post(" + post.getTitle() + ") made by: " + principal.getName() + " message "
					+ ex.getMessage());
			throw ex;
		}
		logger.info("Successfully saved post(" + post.getTitle() + ") made by: " + principal.getName());
		return "Success";
	}
	
	@RequestMapping(value = "/posting/rate", method = RequestMethod.POST)
	@ResponseBody
	public String ratePost(int id, @RequestBody Rating rate, Principal principal) throws Exception {
		try {
			Posting post = daoHelper.getPostingDao().getPostingById(id);
			rate.setDate(Calendar.getInstance());
			rate.setUsername(principal.getName());
			boolean isNew = true;
			for(Rating rating : post.getRatings()){
				if(rating.getUsername().equalsIgnoreCase(rate.getUsername())){
					rating.setDate(rate.getDate());
					rating.setValue(rate.getValue());
					isNew = false;
					break;
				}
			}
			if(isNew){
				post.getRatings().add(rate);
			}
			daoHelper.getPostingDao().save(post);
		} catch (Exception ex) {
			logger.error("error saving post(" + id + ") made by: " + principal.getName() + " message "
					+ ex.getMessage());
			throw ex;
		}
		logger.info("Successfully saved post(" +id + ") made by: " + principal.getName());
		return "Success";
	}

	/**
	 * Find By User
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/posting/findByUser", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getPostingByUser(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> postingsMap = new HashMap<String, Object>();
		ArrayList<Posting> postings = null;
		int numberOfPages = 0;
		try {
			logger.info("retrieving all posts made by: " + principal.getName());
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			postings = daoHelper.getPostingDao().getPostingsByUserID(principal.getName(),
					new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getPostingDao().getCountOfUser(principal.getName()) / (double) pageSize);
		} catch (Exception ex) {
			logger.error("Error retrieving all users made by " + principal.getName());
			throw ex;
		}
		postingsMap.put("list", postings);
		postingsMap.put("numberOfPages", numberOfPages);
		postingsMap.put("currentPage", (pageNo + 1));
		logger.info("successfully retrieved posts made by: " + principal.getName());
		return postingsMap;
	}

	/**
	 * Find By User
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/posting/findHistoryByUser", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getPostingHistorByUser(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> postingsMap = new HashMap<String, Object>();
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		Map<String, String> titleMap = new HashMap<String, String>();
		ArrayList<Posting> postings = null;
		int numberOfPages = 0;

		try {
			logger.info("retrieving all posts made by: " + principal.getName());
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			postings = daoHelper.getPostingDao().getPostingsByUserID(principal.getName(),
					new PageRequest(pageNo, pageSize));

			for (Posting post : postings) {
				int numOfReplies = daoHelper.getRequestDao().getCountForPost(post.getId());
				String title = daoHelper.getServiceDao().getServiceById(post.getServID()).getServiceName();
				countMap.put(post.getId().toString(), numOfReplies);
				titleMap.put(post.getId().toString(), title);
			}

			numberOfPages = (int) Math
					.ceil((double) daoHelper.getPostingDao().getCountOfUser(principal.getName()) / (double) pageSize);
		} catch (Exception ex) {
			logger.error("Error retrieving all users made by " + principal.getName());
			throw ex;
		}
		postingsMap.put("list", postings);
		postingsMap.put("numberOfPages", numberOfPages);
		postingsMap.put("currentPage", (pageNo + 1));
		postingsMap.put("countMap", countMap);
		postingsMap.put("titleMap", titleMap);
		logger.info("successfully retrieved posts made by: " + principal.getName());
		return postingsMap;
	}

	/**
	 * Find By id
	 */
	@RequestMapping(value = "/posting/findById", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getPostingById(Principal principal, int id)  throws Exception{
		Posting posting = null;
		Map<String, Object> map = new HashMap<String, Object>();
		Boolean isOwner = false;
		boolean hasRequested = false;
		Request oldRequestByUser = null;
		int requestID = -1;
		String serviceName = "";
		Double avgRate = 0.0;
		Double avgRateAsPoster = 0.0;
		Integer noOfRatingsAsPoster = 0;
		Double avgRateByUser = 0.0;
		try {
			logger.info("retrieving post by id" + id);
			posting = daoHelper.getPostingDao().getPostingById(id);
			Service service = daoHelper.getServiceDao().getServiceById(posting.getServID());
			serviceName = service.getServiceName();
			avgRate = daoHelper.getPostingDao().getAvgRating(id);
			avgRateAsPoster = daoHelper.getPostingDao().getAvgRatingByUser(posting.getUser().getUsername());
			noOfRatingsAsPoster = daoHelper.getPostingDao().getCountOfUser(posting.getUser().getUsername());
			avgRateByUser = daoHelper.getUserDao().getAvgRating(posting.getUser().getUsername());
			isOwner = posting.getUser().getUsername().equalsIgnoreCase(principal.getName());
			ArrayList<Request>requests = daoHelper.getRequestDao().findByUserAndPost(principal.getName(), posting.getId());
			if(requests.size() >0){
				oldRequestByUser = requests.get(0);

			}
			hasRequested = oldRequestByUser != null;
			
			if(hasRequested){
				requestID = oldRequestByUser.getRequestID();
			}

		} catch (Exception ex) {
			logger.error("Error occurred retrieving post " + ex.getMessage());
			throw ex;
		}
		logger.info("Successfully retrieved post by id " + id);
		map.put("hasRequested", hasRequested);
		map.put("requestID", requestID);
		map.put("posting", posting);
		map.put("isOwner", isOwner);
		map.put("serviceName", serviceName);
		map.put("avgRate", avgRate == null? 0 : avgRate);
		map.put("avgRateByPoster", avgRateAsPoster == null? 0 : avgRateAsPoster);
		map.put("noOfRatingsByPoster", noOfRatingsAsPoster == null? 0 : noOfRatingsAsPoster);
		map.put("avgRateByUser", avgRateByUser == null? 0 : avgRateByUser);

		return map;
	}

	/**
	 * Get all the postings in the db
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/posting/findAll", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllPosting(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "distance", required = false) Double distance) throws Exception {
		Map<String, Object> postingsMap = new HashMap<String, Object>();
		ArrayList<Posting> postings;
		int numberOfPages = 0;

		try {
			logger.info("Retrieving all posts");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			if (distance == null) {
				distance = 25.0;
			}
			User currentUser = daoHelper.getUserDao().findByUserName(principal.getName());
			Double lat = currentUser.getLatitude();
			Double lng = currentUser.getLongitude();
			if (lat == null || lng == null) {
				postings = daoHelper.getPostingDao().getAllPostings(new PageRequest(pageNo, pageSize));
			} else {
				postings = daoHelper.getPostingDao().getPostsByLocation(new PageRequest(pageNo, pageSize),
						currentUser.getLatitude(), currentUser.getLongitude(), distance);
			}
			for(Posting post: postings){
				Double avgRate = daoHelper.getPostingDao().getAvgRating(post.getId());
				postingsMap.put("rate_"+post.getId(), avgRate);
			}

			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math.ceil((double) daoHelper.getPostingDao().getCountOfAll() / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving posts " + ex.getMessage());
			throw ex;
		}
		postingsMap.put("list", postings);
		postingsMap.put("numberOfPages", numberOfPages);
		postingsMap.put("currentPage", (pageNo + 1));

		return postingsMap;
	}

	/**
	 * Get all the postings in the db
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/posting/findAll", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllPostingAdmin(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> postingsMap = new HashMap<String, Object>();
		ArrayList<Posting> postings;
		int numberOfPages = 0;
		try {
			logger.info("Retrieving all posts");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			postings = daoHelper.getPostingDao().getAllPostingsAdmin(new PageRequest(pageNo, pageSize));
			for (Posting post : postings) {
				Integer numOfReplies = daoHelper.getRequestDao().getCountForPost(post.getId());
				Service service = daoHelper.getServiceDao().getServiceByIdAdmin(post.getServID());
				String serviceName = service.getServiceName();
				postingsMap.put("service_" +post.getId(), serviceName);
				postingsMap.put("replies_"+post.getId().toString(), numOfReplies);
			}
			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math.ceil((double) daoHelper.getPostingDao().getCountOfAll() / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving posts " + ex.getMessage());
			throw ex;
		}
		postingsMap.put("list", postings);
		postingsMap.put("numberOfPages", numberOfPages);
		postingsMap.put("currentPage", (pageNo + 1));

		return postingsMap;
	}
	
	@RequestMapping(value = "/admin/posting/like", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getLikePosts(Principal principal, String title,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> postingsMap = new HashMap<String, Object>();
		ArrayList<Posting> postings;
		int numberOfPages = 0;
		try {
			logger.info("Retrieving all posts");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			postings = daoHelper.getPostingDao().getPostingsLikeTitleAdmin(title ,new PageRequest(pageNo, pageSize));
			for (Posting post : postings) {
				Integer numOfReplies = daoHelper.getRequestDao().getCountForPost(post.getId());
				Service service = daoHelper.getServiceDao().getServiceById(post.getServID());
				String serviceName = service.getServiceName();
				postingsMap.put("service_" +post.getId(), serviceName);
				postingsMap.put("replies_"+post.getId().toString(), numOfReplies);	
			}
			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math.ceil((double) daoHelper.getPostingDao().getCountOfPostLikeAdmin(title) / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving posts " + ex.getMessage());
			throw ex;
		}
		postingsMap.put("list", postings);
		postingsMap.put("numberOfPages", numberOfPages);
		postingsMap.put("currentPage", (pageNo + 1));

		return postingsMap;
	}
	
	/**
	 * Get all the postings in the db
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/posting/like", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllPostingLike(Principal principal, String title,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "distance", required = false) Double distance) throws Exception {
		Map<String, Object> postingsMap = new HashMap<String, Object>();
		ArrayList<Posting> postings;
		int numberOfPages = 0;
		try {
			logger.info("Retrieving all posts");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			if (distance == null) {
				distance = 25.0;
			}
			User currentUser = daoHelper.getUserDao().findByUserName(principal.getName());
			Double lat = currentUser.getLatitude();
			Double lng = currentUser.getLongitude();
			if (lat == null || lng == null) {
				//postings = daoHelper.getPostingDao().getAllPostings(new PageRequest(pageNo, pageSize));
				postings = daoHelper.getPostingDao().getPostingsLikeTitle(title ,new PageRequest(pageNo, pageSize));
			} else {
				postings = daoHelper.getPostingDao().getPostsByLocationLikeTitle(title ,new PageRequest(pageNo, pageSize),
						currentUser.getLatitude(), currentUser.getLongitude(), distance);
			}
			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math.ceil((double) daoHelper.getPostingDao().getCountOfAll() / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving posts " + ex.getMessage());
			throw ex;
		}
		postingsMap.put("list", postings);
		postingsMap.put("numberOfPages", numberOfPages);
		postingsMap.put("currentPage", (pageNo + 1));
		
		return postingsMap;
	}
	
	/**
	 * Find By id
	 */
	@RequestMapping(value = "/admin/posting/findById", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getPostingByIdAdmin(Principal principal, Integer id) {
		Posting posting = null;
		Map<String, Object> map = new HashMap<String, Object>();
		Boolean isOwner = false;

		try {
			logger.info("retrieving post by id" + id);
			posting = daoHelper.getPostingDao().getPostingByIdAdmin(id);
			Service service = daoHelper.getServiceDao().getServiceByIdAdmin(posting.getServID());
			String serviceName = service.getServiceName();

			isOwner = posting.getUser().getUsername().equalsIgnoreCase(principal.getName());

			map.put("serviceName", serviceName);

		} catch (Exception ex) {
			logger.error("Error occurred retrieving post " + ex.getMessage());
			return null;
		}
		logger.info("Successfully retrieved post by id " + id);

		map.put("posting", posting);
		map.put("isOwner", isOwner);

		return map;
	}
	
	@RequestMapping(value = "/admin/posting/save", method = RequestMethod.POST)
	@ResponseBody
	public String updatePostAdmin(@RequestBody Posting post, Principal principal) throws Exception {
		try {
			logger.info("saving post(" + post.getTitle() + ") administer by " + principal.getName());
			Posting origPost = null;
			if (post.getId() != null) {
				origPost = daoHelper.getPostingDao().getPostingByIdAdmin(post.getId());

			}
			// means is new
			if (origPost == null) {
				throw new Exception("Post Doesn't exist");
			} else {
				// update current one by saving only the changed values (not
				// null)
				MergeBeanUtil.copyNonNullProperties(post, origPost);

			}
			Service service = daoHelper.getServiceDao().getServiceByIdAdmin(origPost.getServID());
			if(!service.getStatus().equals(ConstantsUtil.CLOSED)){
				daoHelper.getPostingDao().save(origPost);
			}else{
				throw new Exception("[Brij-Message] Service is closed; Can't update posting");
			}
		} catch (Exception ex) {
			logger.error("error saving post(" + post.getTitle() + ") administer by: " + principal.getName() + " message "
					+ ex.getMessage());
			throw ex;
		}
		logger.info("Successfully saved post(" + post.getTitle() + ") administer by: " + principal.getName());
		return "Success";
	}

	@RequestMapping(value = "/posting/delete", method = RequestMethod.GET)
	@ResponseBody
	public String deletePost(int id, Principal principal) throws Exception {
		try {
			User user = daoHelper.getUserDao().findByUserName(principal.getName());
			Posting post = daoHelper.getPostingDao().getPostingById(id);
			logger.info("saving post(" + post.getTitle() + ") made by: " + principal.getName());

			if (user == post.getUser()) { //user created the post
				post.setStatus("closed");

				daoHelper.getPostingDao().save(post);
			} else {
				throw new Exception(ConstantsUtil.EXCEPTION_FLAG + "Error deleting post");
			}
				
		} catch (Exception ex) {
			logger.error("error deleting post made by: " + principal.getName() + " message "
					+ ex.getMessage());
			throw new Exception(ConstantsUtil.EXCEPTION_FLAG + "Error deleting post");
		}
		logger.error("post successfully deleted made by: " + principal.getName());
		return "Success";
	}
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
