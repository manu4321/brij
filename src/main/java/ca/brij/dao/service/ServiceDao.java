package ca.brij.dao.service;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import ca.brij.bean.service.Service;

public interface ServiceDao extends JpaRepository<Service, Long> {
	
	public ArrayList <Service> getAllServices();
	
	public Service getServiceById(@Param("id") int id);
	
	public ArrayList <Service> getAllServicesAdmin(Pageable pageable);
	
	public int countAllServicesAdmin();
	
	public Service getServiceByIdAdmin(@Param("id") int id);
	
	public ArrayList <Service> getServicesLikeNameAdmin(@Param("serviceName") String serviceName, Pageable pageable);

	public int countServicesLikeNameAdmin(@Param("serviceName") String serviceName);


}
