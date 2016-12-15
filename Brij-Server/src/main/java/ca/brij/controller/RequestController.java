package ca.brij.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.brij.bean.posting.Posting;
import ca.brij.bean.request.Request;
import ca.brij.bean.user.User;
import ca.brij.utils.ConstantsUtil;
import ca.brij.utils.DaoHelper;
import ca.brij.utils.MergeBeanUtil;
import ca.brij.utils.NotificationSenderUtil;

@RestController
public class RequestController {

	@Autowired
	public DaoHelper daoHelper;

	@Autowired
	private NotificationSenderUtil notificationUtils;

	/**
	 * Save request. The person who made the request becomes the owner of the
	 * request
	 * 
	 * @param request
	 * @param principal
	 * @return status of request
	 * @throws Exception
	 */
	@RequestMapping(value = "/request/save", method = RequestMethod.POST)
	@ResponseBody
	public String saveRequest(@RequestBody Request request, Principal principal) throws Exception {

		try {
			logger.info("Saving request made by: " + principal.getName());
			request.setUserID(principal.getName());
			User user = daoHelper.getUserDao().findByUserName(principal.getName());
			if(user == null || user.getStatus().equals(ConstantsUtil.INCOMPLETE)){
				throw new Exception(ConstantsUtil.EXCEPTION_FLAG + " User needs to complete account before replying a post");
			}
			// see if the request exist -- get the first one
			ArrayList<Request> requests = daoHelper.getRequestDao().findByUserAndPost(request.getUserID(), request.getPostID());
			Request oldRequest = null;
			if(requests.size() > 0){
				oldRequest = requests.get(0);
			}
			if (oldRequest == null) {
				// if it doesn't set up creation date and assign the new request
				request.setCreationDate(Calendar.getInstance());
				request.setStatus(ConstantsUtil.PENDING);
				oldRequest = request;
			} else {
				// if it old, set up the properties from the new changes to the
				// old
				MergeBeanUtil.copyNonNullProperties(request, oldRequest);
			}
			request = daoHelper.getRequestDao().save(oldRequest);

			Posting post = daoHelper.getPostingDao().getPostingById(request.getPostID());
			if (post != null) {
				notificationUtils.makeNotification(post.getUser().getUsername(), NotificationSenderUtil.REQUEST_TYPE,
						request.getRequestID(), principal.getName() + " have made a request for your post");
			}

		} catch (Exception e) {
			logger.error("Error saving request " + e.getMessage());
			throw e;
		}
		logger.info("Successfully saved request made by " + principal.getName());
		return "Success";
	}

	@RequestMapping(value = "/request/changeStatus", method = RequestMethod.POST)
	@ResponseBody
	public String changeStatus(int id, String status, Principal principal) throws Exception {

		try {
			logger.info("Saving request made by: " + principal.getName());
			Request request = daoHelper.getRequestDao().findById(id);
			if (request == null) {
				throw new Exception("Error - request doesn't exist");
			}
			request.setStatus(status);
			daoHelper.getRequestDao().save(request);
			// once it is saved notify the other user
			String typeOfNotification = NotificationSenderUtil.REQUEST_TYPE;
			if (status.equals(ConstantsUtil.COMPLETE)) {
				typeOfNotification = NotificationSenderUtil.REQUEST_COMPLETE;
			}
			if (principal.getName().equalsIgnoreCase(request.getUserID())) {
				Posting post = daoHelper.getPostingDao().getPostingById(request.getPostID());
				if (post != null) {
					//this is done so the post owner does not rate its own post
					typeOfNotification = NotificationSenderUtil.REQUEST_TYPE;
					notificationUtils.makeNotification(post.getUser().getUsername(), typeOfNotification,
							request.getRequestID(), principal.getName() + " has changed the status of the request to: " + status);
				}
				
			}else{
				notificationUtils.makeNotification(request.getUserID(), typeOfNotification,
						request.getRequestID(), principal.getName() + " has changed the status of the request to: " + status.replace("_", " "));
			
			}

		} catch (Exception e) {
			logger.error("Error saving request " + e.getMessage());
			throw e;
		}
		logger.info("Successfully saved request made by " + principal.getName());
		return "Success";
	}

	/**
	 * Used to update by someone who is not the owner of the post It assumes
	 * that the request has been made
	 * 
	 * @param request
	 * @return status of request
	 * @throws Exception
	 */
	@RequestMapping(value = "/request/edit", method = RequestMethod.POST)
	@ResponseBody
	public String editRequest(@RequestBody Request request) throws Exception {

		try {
			logger.info("Editing request made by: " + request.getUserID());
			if (request.getRequestID() == null || request.getUserID() == null) {
				throw new Exception("No ID or user provided");
			}
			Request oldRequest = daoHelper.getRequestDao().findById(request.getRequestID());
			if (oldRequest == null) {
				oldRequest = request;
			} else {
				MergeBeanUtil.copyNonNullProperties(request, oldRequest);
			}
			daoHelper.getRequestDao().save(oldRequest);
		} catch (Exception e) {
			logger.error("Error editing request " + e.getMessage());
			throw e;
		}
		logger.info("Successfully edit request!");
		return "Success";
	}

	@RequestMapping(value = "/admin/request/findAll", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<Request> findAll() throws Exception {
		ArrayList<Request> requests = null;
		try {
			logger.info("Finding All Requests");
			requests = daoHelper.getRequestDao().findAll();
		} catch (Exception e) {
			logger.error("Error finding all requests " + e.getMessage());
			throw e;
		}
		logger.info("Successfully got all requests");
		return requests;
	}

	@RequestMapping(value = "/request/findById", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findById(int id, Principal principal) throws Exception {
		Map<String, Object> requestDTO = new HashMap<String, Object>();
		Request request = null;
		Posting post = null;
		String serviceName = null;
		User requester = null;
		boolean isOwner = false;
		Double avgRateAsPoster = 0.0;
		Integer noOfRatingsAsPoster = 0;
		Double avgRateAsRequester = 0.0;

		try {
			logger.info("Finding Request by ID: " + id);
			request = daoHelper.getRequestDao().findById(id);
			post = daoHelper.getPostingDao().getPostingById(request.getPostID());
			serviceName = daoHelper.getServiceDao().getServiceById(post.getServID()).getServiceName();
			requester = daoHelper.getUserDao().findByUserName(request.getUserID());
			isOwner = requester.getUsername().equalsIgnoreCase(principal.getName());
			if (!isOwner && !post.getUser().getUsername().equalsIgnoreCase(principal.getName())) {
				throw new Exception("Error - user not allowed");
			}
			if(isOwner){
				noOfRatingsAsPoster = daoHelper.getPostingDao().getCountOfUser(post.getUser().getUsername());
				avgRateAsPoster = daoHelper.getPostingDao().getAvgRatingByUser(post.getUser().getUsername());
				avgRateAsRequester = daoHelper.getUserDao().getAvgRating(post.getUser().getUsername());

			}else{
				noOfRatingsAsPoster = requester.getRatings().size();
				avgRateAsPoster = daoHelper.getPostingDao().getAvgRatingByUser(requester.getUsername());
				avgRateAsRequester = daoHelper.getUserDao().getAvgRating(requester.getUsername());
			}

		} catch (Exception e) {
			logger.error("Error Finding request" + e.getMessage());
			throw e;
		}
		requestDTO.put("request", request);
		requestDTO.put("posting", post);
		requestDTO.put("serviceName", serviceName);
		requestDTO.put("isOwner", isOwner);
		requestDTO.put("requester", requester);
		requestDTO.put("avgRateByPoster", avgRateAsPoster == null? 0 : avgRateAsPoster);
		requestDTO.put("noOfRatingsByPoster", noOfRatingsAsPoster == null? 0 : noOfRatingsAsPoster);
		requestDTO.put("avgRateByUser", avgRateAsRequester == null? 0 : avgRateAsRequester);

		logger.info("Successfully found request by id: " + id);
		return requestDTO;
	}

	/**
	 * Will find all the requests made by the person that requested it.
	 * 
	 * @param principal
	 * @return list of requests
	 * @throws Exception
	 */
	@RequestMapping(value = "/request/findByRequester", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findByRequester(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		ArrayList<Request> requests = null;
		int numberOfPages = 0;
		String postTitle = "";

		try {
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			logger.info("Finding all request made by the requester " + principal.getName());
			Map<Integer, String> titleMap = new HashMap<Integer, String>();
			Map<Integer, String> serviceMap = new HashMap<Integer, String>();

			requests = daoHelper.getRequestDao().findByUser(principal.getName(), new PageRequest(pageNo, pageSize));
			for (Request r : requests) {
				Posting post = daoHelper.getPostingDao().getPostingById(r.getPostID());
				titleMap.put(r.getRequestID(), post.getTitle());
				serviceMap.put(r.getRequestID(), daoHelper.getServiceDao().getServiceById(post.getServID()).getServiceName());
			}
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getRequestDao().getCountForUser(principal.getName()) / (double) pageSize);
			requestMap.put("postTitles", titleMap);
			requestMap.put("serviceTitles", serviceMap);

		} catch (Exception e) {
			logger.error("Error finding all request made by the requester " + e.getMessage());
			throw e;
		}
		logger.info("Successfully retrieved all requests made by : " + principal.getName());
		requestMap.put("list", requests);
		requestMap.put("numberOfPages", numberOfPages);
		requestMap.put("currentPage", (pageNo + 1));

		return requestMap;
	}

	/**
	 * Will find all the requests made by the person passed as a parameter
	 * 
	 * @param UserID
	 * @return list of requests
	 * @throws Exception
	 */
	@RequestMapping(value = "/request/findByUser", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findByUser(String userID,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		ArrayList<Request> requests = null;
		int numberOfPages = 0;

		try {
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			logger.info("Finding all requests made by user " + userID);
			requests = daoHelper.getRequestDao().findByUser(userID, new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getRequestDao().getCountForUser(userID) / (double) pageSize);
		} catch (Exception e) {
			logger.error("Error finding all requests made by user " + userID);
			throw e;
		}
		logger.info("Successfully find all request made by user " + userID);
		requestMap.put("list", requests);
		requestMap.put("numberOfPages", numberOfPages);
		requestMap.put("currentPage", (pageNo + 1));
		return requestMap;
	}

	/**
	 * Will find all the requests made by the person passed as a parameter
	 * 
	 * @param UserID
	 * @return list of requests
	 * @throws Exception
	 */
	@RequestMapping(value = "/request/findByPost", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findByPost(int postID, @RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		ArrayList<Request> requests = null;
		int numberOfPages = 0;
		try {
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 25;
			}
			logger.info("Finding request made for post with ID " + postID);
			requests = daoHelper.getRequestDao().findByPost(postID, new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getRequestDao().getCountForPost(postID) / (double) pageSize);

		} catch (Exception e) {
			logger.error("Error finding requests made for post with ID " + postID + " " + e.getMessage());
			throw e;
		}
		requestMap.put("list", requests);
		requestMap.put("numberOfPages", numberOfPages);
		requestMap.put("currentPage", (pageNo + 1));
		return requestMap;
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
