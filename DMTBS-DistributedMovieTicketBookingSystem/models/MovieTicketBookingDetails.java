package models;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class MovieTicketBookingDetails{

    public static final String SHOW_TIME_MORNING = "Morning";
    public static final String SHOW_TIME_AFTERNOON = "Afternoon";
    public static final String SHOW_TIME_EVENING = "Evening";

    public static final String AVATAR = "Avatar";
    public static final String AVENGERS = "Avengers";
    public static final String TITANIC = "Titanic";

    public static final String HOUSE_FULL = "Can't Book already House full!";


    int capacity;
    int totalBooking;
    String MovieType;
    String MovieDate;
    String MovieTimeSlot;
    ConcurrentHashMap
<String,Integer> customerDetails= new ConcurrentHashMap
<String,Integer>();

    /*
    Constructor
     */

    public MovieTicketBookingDetails(int capacity, int totalBooking,String movieID){
        this.capacity=capacity;
        this.totalBooking=totalBooking;
        this.MovieDate=this.getMovieDateFromMovieId(movieID);
        this.MovieTimeSlot= this.getMovieTimeSlotFromMovieID(movieID.charAt(3));
        this.MovieType=MovieType;
        this.customerDetails = new ConcurrentHashMap
<String,Integer>();
    }

    public String getMovieTimeSlotFromMovieID(char c)
    {
        if(c == 'M')
            return SHOW_TIME_MORNING;
        else if(c == 'A')
            return SHOW_TIME_AFTERNOON;
        else if(c == 'E')
            return SHOW_TIME_EVENING;
        return "Invalid";
    }

    public String getMovieDateFromMovieId(String MovieID)
    {
        return MovieID.substring(4,6)+"/"+MovieID.substring(6,8)+"/"+MovieID.substring(8, 10);
    }

    /*
    Getters
     */
    public int getCapacity() {
        return this.capacity;
    }
    public int getTotalBooking(){return this.totalBooking;}

    public ConcurrentHashMap
<String,Integer> getBookedCustomersIds()
    {
        return this.customerDetails;
    }

    /*
    Setters
     */

    public void setCapacity(int capacity)
    {
        this.capacity=capacity;
    }

    public void setTotalBooking(int totalBooking)
    {
        this.totalBooking=totalBooking;
    }

    public void setBookedCustomerIds(String customerIds,int numberOfTickets )
    {

        this.customerDetails.put(customerIds,numberOfTickets);
        System.out.println(this.customerDetails);

    }

   
    public boolean isHouseFull()
    {
        return this.totalBooking >= this.capacity;
    }

    public void inTotalBooking(int numberOfTickets)
    {
        this.totalBooking=this.totalBooking+numberOfTickets;
    }

    public void dcTotalBooking(int numberOfTickets)
    {
        this.totalBooking=this.totalBooking>0?this.totalBooking-numberOfTickets:0;
    }




}
