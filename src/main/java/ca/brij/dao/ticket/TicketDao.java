package ca.brij.dao.ticket;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import ca.brij.bean.ticket.Ticket;

public interface TicketDao extends JpaRepository<Ticket, Long>{

	public ArrayList<Ticket> getAllTicket(Pageable pageable);
	
	public Ticket getTicketByID(@Param("id") int id);
		
	public int countAllTicket();

	public ArrayList<Ticket> getAllTicketAdmin(Pageable pageable);

	public int countAllTicketAdmin();

	public ArrayList<Ticket> getTicketByUser(@Param("userID") String userID, Pageable pageable);

	public int countTicketByUser(@Param("userID") String userID);
	
	public ArrayList<Ticket> getTicketByTypeAdmin(@Param("type") String type, Pageable pageable);
	
	public int countTicketByTypeAdmin(@Param("type") String type);
	
	public ArrayList<Ticket> getTicketByType(@Param("type") String type, @Param("userID") String userID, Pageable pageable);
	
	public int countTicketByType(@Param("type") String type, @Param("userID") String userID);


}
