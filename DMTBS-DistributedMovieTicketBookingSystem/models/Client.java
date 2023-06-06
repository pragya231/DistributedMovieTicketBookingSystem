package models;


import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Client{
    @WebMethod
    String bookMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets) ;
    @WebMethod
    String getBookingSchedule (String customerID) ;
    @WebMethod
    String cancelMovieTickets (String customerID,String movieID,String movieName,String numberOfTickets) ;
    @WebMethod
    String exchangeTickets(String customerID, String movieID, String movieName, String new_movieID, String new_movieName, int numberOfTickets);

    @WebMethod
    String addMovieSlots (String movieID, String movieName, int capacity) ;
    @WebMethod
    String removeMovieSlots (String movieID,String movieName) ;
    @WebMethod
    String listMovieShowsAvailability (String movieName) ;
    @WebMethod
    String AdminListMovieShowsAvailability(String movieName);
}
