package ca.brij.controller;

import java.util.ArrayList;
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
import ca.brij.bean.service.Service;
import ca.brij.dao.service.ServiceDao;
import ca.brij.utils.ConstantsUtil;
import ca.brij.utils.DaoHelper;
import ca.brij.utils.MergeBeanUtil;

@RestController
public class ServiceController {

	@Autowired
	private DaoHelper daoHelper;

	@RequestMapping(value = "/service/save", method = RequestMethod.POST)
	@ResponseBody
	public String saveService(@RequestBody Service service) throws Exception {

		try {
			logger.info("Saving service");
			service.setStatus(ConstantsUtil.ACTIVE);
			daoHelper.getServiceDao().save(service);

		} catch (Exception e) {
			logger.error("Error saving service " + e.getMessage());
			throw e;
		}
		logger.info("Successfully saved service");
		return "Success";
	}

	@RequestMapping(value = "/service/findAll", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<Service> findAll() throws Exception {
		ArrayList<Service> services = null;
		try {
			logger.info("Finding All Services");
			services = daoHelper.getServiceDao().getAllServices();
		} catch (Exception e) {
			logger.error("Error finding all services " + e.getMessage());
			throw e;
		}
		logger.info("Successfully got all services");
		return services;
	}

	@RequestMapping(value = "/service/findById", method = RequestMethod.GET)
	@ResponseBody
	public Service findByID(int id) throws Exception {
		Service service = null;
		try {
			logger.info("Finding a service by id " + id);
			service = daoHelper.getServiceDao().getServiceById(id);
		} catch (Exception e) {
			logger.error("Error finding a service " + e.getMessage());
			throw e;
		}
		logger.info("Successfully got a service");
		return service;
	}

	@RequestMapping(value = "/admin/service/findAll", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findAllAdmin(@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> serviceMap = new HashMap<String, Object>();
		int numberOfPages = 0;
		ArrayList<Service> services = null;

		try {
			logger.info("Finding All Services");
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			services = daoHelper.getServiceDao().getAllServicesAdmin(new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math.ceil((double) daoHelper.getServiceDao().countAllServicesAdmin() / (double) pageSize);
		} catch (Exception e) {
			logger.error("Error finding all services " + e.getMessage());
			throw e;
		}
		logger.info("Successfully got all services");
		serviceMap.put("list", services);
		serviceMap.put("numberOfPages", numberOfPages);
		serviceMap.put("currentPage", (pageNo + 1));
		return serviceMap;
	}

	@RequestMapping(value = "/admin/service/findById", method = RequestMethod.GET)
	@ResponseBody
	public Service findByIDAdmin(int id) throws Exception {
		Service service = null;
		try {
			logger.info("Finding a service by id " + id);
			service = daoHelper.getServiceDao().getServiceByIdAdmin(id);
		} catch (Exception e) {
			logger.error("Error finding a service " + e.getMessage());
			throw e;
		}
		logger.info("Successfully got a service");
		return service;
	}

	@RequestMapping(value = "/admin/service/findLikeTitle", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findLikeTitleAdmin(String serviceName,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
		Map<String, Object> serviceMap = new HashMap<String, Object>();
		int numberOfPages = 0;
		ArrayList<Service> services = null;
		try {
			logger.info("Finding a service by id " + serviceName);
			if (pageNo == null) {
				pageNo = 0;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			services = daoHelper.getServiceDao().getServicesLikeNameAdmin(serviceName, new PageRequest(pageNo, pageSize));
			numberOfPages = (int) Math.ceil((double) daoHelper.getServiceDao().countServicesLikeNameAdmin(serviceName) / (double) pageSize);

		} catch (Exception e) {
			logger.error("Error finding a service " + e.getMessage());
			throw e;
		}
		logger.info("Successfully got a service");
		serviceMap.put("list", services);
		serviceMap.put("numberOfPages", numberOfPages);
		serviceMap.put("currentPage", (pageNo + 1));
		return serviceMap;
	}
	@RequestMapping(value = "/admin/service/save", method = RequestMethod.POST)
	@ResponseBody
	public String saveServiceAdmin(@RequestBody Service service) throws Exception {

		try {
			logger.info("Saving service");
			Service oldService = daoHelper.getServiceDao().getServiceByIdAdmin(service.getId());
			if(oldService == null){
				throw new Exception("Service Doesn't exist");
			}else{
				MergeBeanUtil.copyNonNullProperties(service, oldService);
			}
			daoHelper.getPostingDao().changeState(oldService.getStatus(), oldService.getId());
			ArrayList<Posting> postingList = daoHelper.getPostingDao().getPostingsByServID(oldService.getId());
			for(Posting post: postingList){
				daoHelper.getRequestDao().changeState(oldService.getStatus(), post.getId());
			}
			daoHelper.getServiceDao().save(oldService);

		} catch (Exception e) {
			logger.error("Error saving service " + e.getMessage());
			throw e;
		}
		logger.info("Successfully saved service");
		return "Success";
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
