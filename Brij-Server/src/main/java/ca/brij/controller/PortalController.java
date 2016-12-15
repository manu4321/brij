package ca.brij.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.brij.bean.user.User;
import ca.brij.bean.user.UserRole;
import ca.brij.utils.ApplicationProperties;
import ca.brij.utils.DaoHelper;

@Controller
public class PortalController {
	
	@Autowired
	public DaoHelper daoHelper;
	@Autowired
	public ApplicationProperties appProperties;
	
	@RequestMapping("/")
	public ModelAndView startup(Model model, Principal principal) throws IOException{
		String url = "/webPortal/portal.html";
		ModelAndView mv = new ModelAndView(url);
		return mv;
		
	}
	
	@RequestMapping("/webPortal/connect")
	public ModelAndView login(Model model, Principal principal) throws IOException{
		String url = "/login.html";
		if(principal != null){
			url = "redirect:/homePage";
		}
		ModelAndView mv = new ModelAndView(url);
		return mv;
		
	}
	
	@RequestMapping("/userPortal/home")
	public ModelAndView goToWebPortal(Model model, Principal principal) throws IOException{
		String portal = "userPortal.html";
		ModelAndView mv = new ModelAndView(portal);
		return mv;
	}
	
	@RequestMapping("/userPortal/accountDetail")
	public ModelAndView goToAccountDetails(Model model, Principal principal) throws IOException{
		String url = "accountDetails.html";
		ModelAndView mv = new ModelAndView(url);
		return mv;
	}
	
	@RequestMapping("/homePage")
	public ModelAndView goToHome(Model model, Principal principal) throws IOException{
		
		String portal = "redirect:/userPortal/home";
		if(isAdmin(principal.getName())){
			portal = "/admin/adminConsole.html";
		}
		ModelAndView mv = new ModelAndView(portal);
		return mv;
	}

	@RequestMapping("/resetpassword")
	public ModelAndView goToUsers(String resetid) throws IOException{
		ModelAndView mv = new ModelAndView("reset.html" + "?resetid=" + resetid);
		return mv;
	}
	
	
	@RequestMapping("/userPortal/ticketPage")
	public ModelAndView goToUserTickets(Model model) throws IOException{
		ModelAndView mv = new ModelAndView("/userPortal/ticketsPage.html");
		return mv;
	}
	@RequestMapping("/webPortal/desktop")
	public ModelAndView openDesktop(Model model, Principal principal) throws IOException{

		ModelAndView mv = new ModelAndView("redirect:" + appProperties.getDesktopURL());
		return mv;
	}
	
	/**
	 * FOR ADMIN
	 */
	
	@RequestMapping("/admin/userPage")
	public ModelAndView goToUsers(Model model, Principal principal) throws IOException{
		String goTo = "/admin/usersPage.html";
		if(!isAdmin(principal.getName())){
			goTo = "redirect:/userPortal/home";
		}
		ModelAndView mv = new ModelAndView(goTo);
		return mv;
	}
	
	@RequestMapping("/admin/postPage")
	public ModelAndView goToPosts(Model model, Principal principal) throws IOException{
		String goTo = "/admin/postsPage.html";
		if(!isAdmin(principal.getName())){
			goTo = "redirect:/userPortal/home";
		}
		ModelAndView mv = new ModelAndView(goTo);
		return mv;
	}
	
	@RequestMapping("/admin/servicePage")
	public ModelAndView goToServices(Model model, Principal principal) throws IOException{
		String goTo = "/admin/servicesPage.html";
		if(!isAdmin(principal.getName())){
			goTo = "redirect:/userPortal/home";
		}
		ModelAndView mv = new ModelAndView(goTo);
		return mv;
	}
	
	@RequestMapping("/admin/ticketPage")
	public ModelAndView goToTickets(Model model, Principal principal) throws IOException{
		String goTo = "/admin/ticketsPage.html";
		if(!isAdmin(principal.getName())){
			goTo = "redirect:/userPortal/home";
		}
		ModelAndView mv = new ModelAndView(goTo);
		return mv;
	}

	@RequestMapping("/admin/reportPage")
	public ModelAndView goToReports(Model model, Principal principal) throws IOException{
		String goTo = "/admin/reportsPage.html";
		if(!isAdmin(principal.getName())){
			goTo = "redirect:/userPortal/home";
		}
		ModelAndView mv = new ModelAndView(goTo);
		return mv;
	}
	
	public boolean isAdmin(String username){
		User currentUser = daoHelper.getUserDao().findByUserName(username);
		Set<UserRole> roles = currentUser.getUserRole();
		for(UserRole role : roles){
			if(role.getRole().equals(UserRole.ADMIN)){
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping(value = "/download/android", method = RequestMethod.GET, produces="application/apk")
	@ResponseBody
	public ResponseEntity<InputStreamResource> getAndroid() throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream file = classLoader.getResourceAsStream("brij.apk");
	    InputStreamResource isResource = new InputStreamResource(file);
	 	HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
	    headers.add("Pragma", "no-cache");
	    headers.add("Expires", "0");
	    headers.setContentDispositionFormData("attachment", "brij.apk");
	    return new ResponseEntity<InputStreamResource>(isResource, headers, HttpStatus.OK);
	}
}
