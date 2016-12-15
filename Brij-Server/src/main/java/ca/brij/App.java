package ca.brij;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ca.brij.bean.service.Service;
import ca.brij.bean.user.User;
import ca.brij.dao.service.ServiceDao;
import ca.brij.dao.user.UserDao;

/**
 * Main Class
 *
 */
@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
    	logger.info("Starting App");
		SpringApplication.run(App.class, args);
    }
    
	@Autowired
    public void insertPriviligedUser(UserDao dao){
		if(dao.findByUserName("admin") == null){
	    	dao.save(User.getPriviligedUser());
		}
    }
	
	@Autowired
    public void insertServices(ServiceDao dao){
		if(dao.getAllServices().size() == 0){
	    	dao.save(Service.populateServices());
		}
    }
	
	@PreDestroy
	public void beforeShutDown(){
		logger.info("Server is shutting down... bye bye!!!");
	}
    private final static Logger logger = LoggerFactory.getLogger(App.class);


}
