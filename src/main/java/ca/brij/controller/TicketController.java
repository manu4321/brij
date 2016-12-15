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

import ca.brij.bean.conversation.Message;
import ca.brij.bean.ticket.Ticket;
import ca.brij.utils.ConstantsUtil;
import ca.brij.utils.DaoHelper;
import ca.brij.utils.MergeBeanUtil;

@RestController
public class TicketController {
	@Autowired
	private DaoHelper daoHelper;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/ticket/save", method = RequestMethod.POST)
	@ResponseBody
	public String updateTicket(@RequestBody Ticket ticket, Principal principal) throws Exception {
		try {
			logger.info("saving ticket(" + ticket.getId() + ") made by: " + principal.getName());
			Ticket oldTicket = daoHelper.getTicketDao().getTicketByID(ticket.getId());
			if (oldTicket == null) {
				oldTicket = ticket;
				oldTicket.setUserID(principal.getName());
				oldTicket.setStatus(ConstantsUtil.ACTIVE);
				oldTicket.setCreationDate(Calendar.getInstance());
			} else {
				if(oldTicket.getStatus().equals(ConstantsUtil.CLOSED)){
					throw new Exception("Ticket cant be updated... it is closed");
				}
				MergeBeanUtil.copyNonNullProperties(ticket, oldTicket);
			}
			daoHelper.getTicketDao().save(ticket);
		} catch (Exception ex) {
			logger.info("error saving ticket(" + ticket.getId() + ") made by: " + principal.getName());
			throw ex;
		}
		logger.info("Successfully saving ticket(" + ticket.getId() + ") made by: " + principal.getName());
		return "Success";
	}
	
	@RequestMapping(value = "/ticket/saveMessage", method = RequestMethod.POST)
	@ResponseBody
	public String saveMessage(Integer id, @RequestBody Message message, Principal principal) throws Exception {

		try {
			message.setUsername(principal.getName());
			message.setDate(Calendar.getInstance());
			Ticket ticket = daoHelper.getTicketDao().getTicketByID(id);
			if(ticket  == null){
				throw new Exception("Error -- ticket doesn't exist");
			}else if(ticket.getStatus().equals(ConstantsUtil.CLOSED)){
				throw new Exception("Error -- Ticket is closed");
			}
			//add the new message
			ticket.getMessages().add(message);
			daoHelper.getTicketDao().save(ticket);
		} catch (Exception e) {
			logger.error("Error occurred retrieving ticket by request " + e.getMessage());
			throw e;

		}
		return "success";
	}
	
	@RequestMapping(value = "/ticket/updateStatus", method = RequestMethod.POST)
	@ResponseBody
	public String updateStatus(Integer id, @RequestBody Message message, String status ,Principal principal) throws Exception {

		try {
			message.setUsername(principal.getName());
			message.setDate(Calendar.getInstance());
			Ticket ticket = daoHelper.getTicketDao().getTicketByID(id);
			if(ticket  == null){
				throw new Exception("Error -- ticket doesn't exist");
			}
			//add the new message
			ticket.setStatus(status);
			ticket.getMessages().add(message);
			daoHelper.getTicketDao().save(ticket);
		} catch (Exception e) {
			logger.error("Error occurred retrieving ticket by request " + e.getMessage());
			throw e;

		}
		return "success";
	}

	/**
	 * Find By User
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/ticket/findByUser", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTicketByUser(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> ticketMap = new HashMap<String, Object>();
		ArrayList<Ticket> tickets = null;
		int numberOfPages = 0;
		try {
			logger.info("retrieving all tickets made by: " + principal.getName());
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			tickets = daoHelper.getTicketDao().getTicketByUser(principal.getName(), new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getTicketDao().countTicketByUser(principal.getName()) / (double) pageSize);
		} catch (Exception ex) {
			logger.error("Error retrieving all tickets made by " + principal.getName());
			throw ex;
		}
		ticketMap.put("list", tickets);
		ticketMap.put("numberOfPages", numberOfPages);
		ticketMap.put("currentPage", (pageNo + 1));
		logger.info("successfully retrieved tickets made by: " + principal.getName());
		return ticketMap;
	}

	/**
	 * Find By id
	 */
	@RequestMapping(value = "/ticket/findById", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTicketById(Principal principal, int id) {
		Ticket ticket = null;
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			logger.info("retrieving post by id" + id);
			ticket = daoHelper.getTicketDao().getTicketByID(id);
		} catch (Exception ex) {
			logger.error("Error occurred retrieving post " + ex.getMessage());
			return null;
		}
		logger.info("Successfully retrieved post by id " + id);
		map.put("ticket", ticket);

		return map;
	}
	@RequestMapping(value = "/ticket/byType", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTicketByType(Principal principal, String type,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> ticketsMap = new HashMap<String, Object>();
		ArrayList<Ticket> tickets;
		int numberOfPages = 0;
		try {
			logger.info("Retrieving all tickets by type");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			tickets = daoHelper.getTicketDao().getTicketByType(type, principal.getName(), new PageRequest(pageNo, pageSize));
			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getTicketDao().countTicketByType(type, principal.getName()) / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving ticckets " + ex.getMessage());
			throw ex;
		}
		ticketsMap.put("list", tickets);
		ticketsMap.put("numberOfPages", numberOfPages);
		ticketsMap.put("currentPage", (pageNo + 1));

		return ticketsMap;
	}

	/**
	 * Get all the Ticket in the db
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/ticket/findAll", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllTicket(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "distance", required = false) Double distance) throws Exception {
		Map<String, Object> ticketMap = new HashMap<String, Object>();
		ArrayList<Ticket> tickets;
		int numberOfPages = 0;
		try {
			logger.info("Retrieving all posts");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			tickets = daoHelper.getTicketDao().getAllTicket(new PageRequest(pageNo, pageSize));
			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math.ceil((double) daoHelper.getTicketDao().countAllTicket() / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving posts " + ex.getMessage());
			throw ex;
		}
		ticketMap.put("list", tickets);
		ticketMap.put("numberOfPages", numberOfPages);
		ticketMap.put("currentPage", (pageNo + 1));

		return ticketMap;
	}

	/**
	 * Get all the tickets in the db
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/ticket/findAll", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllticketAdmin(Principal principal,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> ticketMap = new HashMap<String, Object>();
		ArrayList<Ticket> tickets;
		int numberOfPages = 0;
		try {
			logger.info("Retrieving all posts");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			tickets = daoHelper.getTicketDao().getAllTicketAdmin(new PageRequest(pageNo, pageSize));
			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getTicketDao().countAllTicketAdmin() / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving posts " + ex.getMessage());
			throw ex;
		}
		ticketMap.put("list", tickets);
		ticketMap.put("numberOfPages", numberOfPages);
		ticketMap.put("currentPage", (pageNo + 1));

		return ticketMap;
	}

	@RequestMapping(value = "/admin/ticket/byType", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTicketByTypeAdmin(Principal principal, String type,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> ticketsMap = new HashMap<String, Object>();
		ArrayList<Ticket> tickets;
		int numberOfPages = 0;
		try {
			logger.info("Retrieving all tickets by type");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			tickets = daoHelper.getTicketDao().getTicketByTypeAdmin(type, new PageRequest(pageNo, pageSize));
			// divide then take the decimal away. Ex 10.5 will give 10
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getTicketDao().countTicketByTypeAdmin(type) / (double) pageSize);

		} catch (Exception ex) {
			logger.error("Failed retrieving ticckets " + ex.getMessage());
			throw ex;
		}
		ticketsMap.put("list", tickets);
		ticketsMap.put("numberOfPages", numberOfPages);
		ticketsMap.put("currentPage", (pageNo + 1));

		return ticketsMap;
	}

	/**
	 * Find By id
	 */
	@RequestMapping(value = "/admin/ticket/findById", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTicketById(Principal principal, Integer id) {
		Ticket ticket = null;
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			logger.info("retrieving post by id" + id);
			ticket = daoHelper.getTicketDao().getTicketByID(id);
		} catch (Exception ex) {
			logger.error("Error occurred retrieving post " + ex.getMessage());
			return null;
		}
		logger.info("Successfully retrieved post by id " + id);
		map.put("ticket", ticket);

		return map;
	}

	/**
	 * Find By User
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/ticket/findByUser", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTicketByUserAdmin(String username,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> ticketMap = new HashMap<String, Object>();
		ArrayList<Ticket> tickets = null;
		int numberOfPages = 0;
		try {
			logger.info("retrieving all tickets made by: " + username);
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			tickets = daoHelper.getTicketDao().getTicketByUser(username, new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math
					.ceil((double) daoHelper.getTicketDao().countTicketByUser(username) / (double) pageSize);
		} catch (Exception ex) {
			logger.error("Error retrieving all tickets made by " + username);
			throw ex;
		}
		ticketMap.put("list", tickets);
		ticketMap.put("numberOfPages", numberOfPages);
		ticketMap.put("currentPage", (pageNo + 1));
		logger.info("successfully retrieved tickets made by: " + username);
		return ticketMap;
	}

	@RequestMapping(value = "/admin/ticket/save", method = RequestMethod.POST)
	@ResponseBody
	public String updateTicketAdmin(@RequestBody Ticket ticket, Principal principal) throws Exception {
		try {
			logger.info("saving ticket(" + ticket.getId() + ") made by: " + principal.getName());
			Ticket oldTicket = daoHelper.getTicketDao().getTicketByID(ticket.getId());
			if (oldTicket == null) {
				throw new Exception("Error: Ticket does not exist");
			} else {
				MergeBeanUtil.copyNonNullProperties(ticket, oldTicket);
			}
			daoHelper.getTicketDao().save(ticket);
		} catch (Exception ex) {
			logger.info("error saving ticket(" + ticket.getId() + ") made by: " + principal.getName());
			throw ex;
		}
		logger.info("Successfully saving ticket(" + ticket.getId() + ") made by: " + principal.getName());
		return "Success";
	}
}
