package frontend;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface FrontEndInterface {

	
	@WebMethod
	String addMovieSlots (String UserID,String movieID, String movieName, int bookingCapacity) throws IOException, ExecutionException, InterruptedException;
	
	@WebMethod
	String removeMovieSlots (String UserID,String movieID, String movieName) throws IOException, InterruptedException;
	
	@WebMethod
	String listMovieShowsAvailability (String UserID,String movieName) throws IOException, InterruptedException;
	
	@WebMethod
	String seeAll() ;
	//Admin and Cx methods
	@WebMethod
	String bookMovieTickets (String customerID, String  movieID, String movieName, int numberOfTickets) throws IOException, InterruptedException;
	@WebMethod
	String getBookingSchedule (String customerID) throws IOException, InterruptedException;
	@WebMethod
	String cancelMovieTickets (String customerID,String movieID, String movieName, int numberOfTickets) throws IOException, InterruptedException;
	@WebMethod
	String exchangeTickets (String customerID, String movieID,String movieName, String new_movieID, String new_movieName, int numberOfTickets) throws IOException, InterruptedException;
}
