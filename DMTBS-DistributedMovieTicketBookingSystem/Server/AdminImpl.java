package Server;


import FinalVariables.FinalVariables;
import models.Client;
import models.MovieTicketBookingDetails;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import static Server.AdminImpl.UDPSendRequest;
import java.util.logging.Logger;

@WebService(endpointInterface = "models.Client")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class AdminImpl implements Client {
    private static Logger logger = Logger.getLogger("ServerLogger");

    public static void LoggerInitialization2(String ServerName){
        FileHandler fh;

        try {
            fh = new FileHandler("./DMTBS/src/Logs/Server/"+ServerName+".log");
            logger.setUseParentHandlers(false);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.info(ServerName+ " Logger Initialized");

        } catch (SecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static final int ATW_UDP_portNum = 3336;
    public static final int VER_UDP_portNum = 3337;
    public static final int OUT_UDP_portNum = 3338;
    public String ServerName;
    /*

    HashMap for Storing customers Data who had booked the tickets on Atwater server
    Movie name,l movieid, object
     */
    public ConcurrentHashMap
            <String, ConcurrentHashMap
                    <String, MovieTicketBookingDetails>> movieSlots;
    ArrayList<String> Servers = new ArrayList<>(Arrays.asList("atwater", "verdun", "outremont"));

    protected AdminImpl(String ServerName)  {
        this.ServerName = ServerName;
        movieSlots = new ConcurrentHashMap
                <>();

        movieSlots.put("Avengers",new ConcurrentHashMap<String,MovieTicketBookingDetails>());
        movieSlots.put("Avatar",new ConcurrentHashMap<String,MovieTicketBookingDetails>());
        movieSlots.put("Titanic",new ConcurrentHashMap<String,MovieTicketBookingDetails>());
//        LoggerInitialization2(ServerName);

    }

    public static int getUDPPortNumber(String serv_name) {
        if (serv_name.equals("atwater"))
            return ATW_UDP_portNum;
        if (serv_name.equals("verdun"))
            return VER_UDP_portNum;
        if (serv_name.equals("outremont"))
            return OUT_UDP_portNum;
        return -1;
    }

    public static String UDPSendRequest(String data, int port) {

        logger.info("Server"+ " is sending UDP Request to port "+port + " with data: "+ data);
        String result = "";
        try (DatagramSocket aSocket = new DatagramSocket()) {
//                System.out.println("jfffhf"+data.getBytes().length+data);
//            DatagramPacket request = new DatagramPacket(data.getBytes(), data.getBytes().length,
//                    InetAddress.getByName("localhost"), port);

            DatagramPacket request = new DatagramPacket(data.getBytes(),data.length(), InetAddress.getByName("localhost"), port);
//            socket.send(rtsequencer);
            aSocket.send(request);
//                System.out.println("data sent to " + port);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            System.out.println("Waiting for reply");
            aSocket.receive(reply);
            aSocket.close();
            result = new String(reply.getData()).trim();
            System.out.println("Result: " + result);
            logger.info(" received data: "+ result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    /*
     Customers Functionality
      */
    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {

        logger.info("client has request to call"+" book movie tickets " + " with data: "+ customerID+" "+ movieID+" "+ numberOfTickets+" "+movieName );


        String BookingServer = movieID.substring(0, 3);
        String BookingServerport = null;
        String CustomerServer = customerID.substring(0, 3);
        String result = "You can't book";
        switch (BookingServer) {
            case "ATW":
                BookingServerport = "atwater";
                break;
            case "VER":
                BookingServerport = "verdun";
                break;
            case "OUT":
                BookingServerport = "outremont";
                break;
        }


        boolean hasCustomerBookedMovieInVerdun;
        logger.info(ServerName+ "has received request to call"+" hasCutomerBookedMovieInCurrentServer " + " with data: "+ customerID+" "+ movieID+" "+movieName );

        MainThread mainThread10 = new MainThread("hasCutomerBookedMovieInCurrentServer:" + customerID + " " + movieID + " " + movieName, getUDPPortNumber("verdun"));
        Thread thread10 = new Thread(mainThread10);
        thread10.start();
        try {
            thread10.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        hasCustomerBookedMovieInVerdun = Boolean.parseBoolean(mainThread10.getValue());
        logger.info("received "+ hasCustomerBookedMovieInVerdun );

        boolean hasCustomerBookedMovieInAccoutrement;
        logger.info(ServerName+ "has received request to call"+" hasCutomerBookedMovieInCurrentServer " + " with data: "+ customerID+" "+ movieID+" "+movieName );

        MainThread MainThread0 = new MainThread("hasCutomerBookedMovieInCurrentServer:" + customerID + " " + movieID + " " + movieName, getUDPPortNumber("outremont"));
        Thread thread20 = new Thread(MainThread0);
        thread20.start();
        try {
            thread20.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        hasCustomerBookedMovieInAccoutrement = Boolean.parseBoolean(MainThread0.getValue());
        logger.info("received "+ hasCustomerBookedMovieInAccoutrement );

        boolean hasCustomerBookedMovieInAtwater;
        logger.info(ServerName+ "has received request to call"+" hasCutomerBookedMovieInCurrentServer " + " with data: "+ customerID+" "+ movieID+" "+movieName );

        MainThread mainThread30 = new MainThread("hasCutomerBookedMovieInCurrentServer:" + customerID + " " + movieID + " " + movieName, getUDPPortNumber("atwater"));
        Thread thread30 = new Thread(mainThread30);
        thread30.start();
        try {
            thread30.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        hasCustomerBookedMovieInAtwater = Boolean.parseBoolean(mainThread30.getValue());
        logger.info("received "+ hasCustomerBookedMovieInAtwater );
        int bookingsInOtherAreas = 0;

        switch (CustomerServer) {
            case "ATW": {
                logger.info(ServerName+ "has received request to call"+" getTotalBookingForUserForCurrentServer " + " with data: "+ customerID );

                MainThread mainThread4 = new MainThread("getTotalBookingForUserForCurrentServer:" + customerID, getUDPPortNumber("verdun"));
                Thread thread4 = new Thread(mainThread4);
                thread4.start();
                try {
                    thread4.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bookingsInOtherAreas += Integer.parseInt(mainThread4.getValue());
                logger.info("received "+ bookingsInOtherAreas );
                logger.info(ServerName+ "has received request to call"+" getTotalBookingForUserForCurrentServer " + " with data: "+ customerID );

                MainThread mainThread5 = new MainThread("getTotalBookingForUserForCurrentServer:" + customerID, getUDPPortNumber("outremont"));
                Thread thread5 = new Thread(mainThread5);
                thread5.start();
                try {
                    thread5.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bookingsInOtherAreas += Integer.parseInt(mainThread5.getValue());
                logger.info("received "+ bookingsInOtherAreas );


                break;
            }
            case "VER": {
                logger.info(ServerName+ "has received request to call"+" getTotalBookingForUserForCurrentServer " + " with data: "+ customerID );

                MainThread mainThread4 = new MainThread("getTotalBookingForUserForCurrentServer:" + customerID, getUDPPortNumber("atwater"));
                Thread thread4 = new Thread(mainThread4);
                thread4.start();
                try {
                    thread4.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bookingsInOtherAreas += Integer.parseInt(mainThread4.getValue());
                logger.info("received "+ bookingsInOtherAreas );

                logger.info(ServerName+ "has received request to call"+" getTotalBookingForUserForCurrentServer " + " with data: "+ customerID );

                MainThread mainThread5 = new MainThread("getTotalBookingForUserForCurrentServer:" + customerID, getUDPPortNumber("outremont"));
                Thread thread5 = new Thread(mainThread5);
                thread5.start();
                try {
                    thread5.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bookingsInOtherAreas += Integer.parseInt(mainThread5.getValue());
                logger.info("received "+ bookingsInOtherAreas );

                break;
            }
            case "OUT": {
                logger.info(ServerName+ "has received request to call"+" getTotalBookingForUserForCurrentServer " + " with data: "+ customerID );

                MainThread mainThread4 = new MainThread("getTotalBookingForUserForCurrentServer:" + customerID, getUDPPortNumber("atwater"));
                Thread thread4 = new Thread(mainThread4);
                thread4.start();
                try {
                    thread4.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bookingsInOtherAreas += Integer.parseInt(mainThread4.getValue());
                logger.info("received "+ bookingsInOtherAreas );

                logger.info(ServerName+ "has received request to call"+" getTotalBookingForUserForCurrentServer " + " with data: "+ customerID );

                MainThread mainThread5 = new MainThread("getTotalBookingForUserForCurrentServer:" + customerID, getUDPPortNumber("verdun"));
                Thread thread5 = new Thread(mainThread5);
                thread5.start();
                try {
                    thread5.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bookingsInOtherAreas += Integer.parseInt(mainThread5.getValue());
                logger.info("received "+ bookingsInOtherAreas );

                break;
            }
        }


        if (BookingServer.equals("ATW") && bookingsInOtherAreas < 3 && !hasCustomerBookedMovieInVerdun
                && !hasCustomerBookedMovieInAccoutrement) {

            MainThread mainThread6 = new MainThread("bookMovieTickets:" + customerID + " " + movieName + " " + movieID + " " + numberOfTickets, getUDPPortNumber(BookingServerport));
            Thread thread6 = new Thread(mainThread6);
            thread6.start();
            try {
                thread6.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            result = mainThread6.getValue();
        } else if (BookingServer.equals("VER") && bookingsInOtherAreas < 3 && !hasCustomerBookedMovieInAtwater
                && !hasCustomerBookedMovieInAccoutrement) {
            MainThread mainThread6 = new MainThread("bookMovieTickets:" + customerID + " " + movieName + " " + movieID + " " + numberOfTickets, getUDPPortNumber(BookingServerport));
            Thread thread6 = new Thread(mainThread6);
            thread6.start();
            try {
                thread6.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            result = mainThread6.getValue();
        } else if (BookingServer.equals("OUT") && bookingsInOtherAreas < 3 && !hasCustomerBookedMovieInAtwater
                && !hasCustomerBookedMovieInVerdun) {
            MainThread mainThread6 = new MainThread("bookMovieTickets:" + customerID + " " + movieName + " " + movieID + " " + numberOfTickets, getUDPPortNumber(BookingServerport));
            Thread thread6 = new Thread(mainThread6);
            thread6.start();
            try {
                thread6.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            result = mainThread6.getValue();
        }

        logger.info("received "+ result );


        return result;

    }

    @Override
    public String getBookingSchedule(String customerID)  {
        logger.info("client has request to call"+" getBookingSchedule " + " with data: "+ customerID );

        StringBuilder result = new StringBuilder();
        for (String server : Servers) {
            MainThread mainThread = new MainThread("getBookingSchedule:" + customerID, getUDPPortNumber(server));
            Thread thread = new Thread(mainThread);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            result.append(mainThread.getValue());
            if (result.toString().equals("") || result.toString().equals("\n")) {

            } else {
                result.append("\n");
            }
        }
        if (result.toString().equals("") || result.toString().equals("\n")) {
            result = new StringBuilder("No Schedule");
        }

        logger.info("received "+ result.toString() );
        return result.toString();

    }


    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, String numberOfTickets)  {

        logger.info("client has request to call"+" cancelMovieTickets " + " with data: "+ customerID + " "+ movieID+ " "+movieName+" "+ numberOfTickets);

        String Server = movieID.substring(0, 3);

        String result = "";
        switch (Server) {
            case "ATW":
                Server = "atwater";
                break;
            case "VER":
                Server = "verdun";
                break;
            case "OUT":
                Server = "outremont";
                break;
        }

        MainThread mainThread = new MainThread("cancelMovieTickets:" + customerID + " " + movieID + " " + movieName + " " + numberOfTickets, getUDPPortNumber(Server));
        Thread thread = new Thread(mainThread);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        result = result + mainThread.getValue();

        logger.info("received "+ result );
        return result;
    }

    @Override
    public String exchangeTickets(String customerID, String movieID, String movieName, String new_movieID, String new_movieName, int numberOfTickets) {

        String result="";

        result = cancelMovieTickets(customerID,movieID,movieName,String.valueOf(numberOfTickets));


        if (result.equals("Movie slot canceled successfully.")) {

            result=bookMovieTickets(customerID, new_movieID, new_movieName, numberOfTickets);

            if(!result.equals("Movie slot booked successfully."))
            {
                result=bookMovieTickets(customerID,movieID,movieName,numberOfTickets);
                logger.info("received "+ "Every thing got settled and no exchange is done." );
                return "Every thing got settled and no exchange is done.";

            }
            else{
                logger.info("received "+ "Exchange successfully!!!" );
                return "Exchange successfully!!!";

            }
        }

        return result;
    }



    /*
    Admin Functionality
     */
    @Override
    public String addMovieSlots(String movieID, String movieName, int capacity) {

        logger.info("client has request to call"+" addMovieSlots " + " with data: "+ movieName + " "+ movieID+" "+ capacity);

        MovieTicketBookingDetails slot = new MovieTicketBookingDetails(capacity, 0, movieID);

        if (!movieSlots.containsKey(movieName)) {
            movieSlots.put(movieName, new ConcurrentHashMap
                    <>());
        }
        movieSlots.get(movieName).put(movieID, slot);
        logger.info(movieSlots.toString());
        System.out.println("Movie slot added successfully.");
        logger.info("received "+ "Movie slot added successfully.");
        return "Movie slot added successfully.";
    }

    @Override
    public String removeMovieSlots(String movieID, String movieName) {

        logger.info("client has request to call"+ " removeMovieSlots " + " with data: "+ movieName + " "+ movieID);


        if (movieSlots.containsKey(movieName)) {

            ArrayList<String> sortedMovieIds = new ArrayList<>(movieSlots.get(movieName).keySet());
            sortedMovieIds.sort(new DateComparator());

            int index = sortedMovieIds.indexOf(movieID);
            int length = sortedMovieIds.size();

            if (index + 1 < length) {
                final String[] nextMovieId = {sortedMovieIds.get(index + 1)};
                MovieTicketBookingDetails deletedMovieTicketDetails = movieSlots.get(movieName).remove(movieID);
                deletedMovieTicketDetails.getBookedCustomersIds().forEach((k, v) ->
                {
                    try {

                        String r = bookMovieTickets(k, nextMovieId[0], movieName, v);


                        if (r.equals("You can't book")) {
                            nextMovieId[0] = sortedMovieIds.get(index + 1);
                            bookMovieTickets(k, nextMovieId[0], movieName, v);
                        } else {
                            System.out.println("movie booked in another slot successfully!!");
                            logger.info("movie booked in another slot successfully!!" );
                        }
//                        System.out.println(bookMovieTickets(k, nextMovieId, movieName,v));
                    } catch (Exception e) {
                        //throw new RuntimeException(e);
                    }
                });
                logger.info("Movie removed successfully" );
                return "Movie removed successfully";
            }
            logger.info("Movie not removed, No next slot available" );
            return "Movie not removed, No next slot available";


//            movieSlots.get(movieName).remove(movieID);
        }
        logger.info("Movie slot don't exist! please try another ID." );
        return "Movie slot don't exist! please try another ID.";
    }

    @Override
    public String listMovieShowsAvailability(String movieName)  {

        logger.info("client has request to call"+" listMovieShowsAvailability " + " with data: "+ movieName );


        try {
            StringBuilder result = new StringBuilder();
            result.append("");
            if (movieSlots.size() > 0) {
                ConcurrentHashMap
                        <String, MovieTicketBookingDetails> listMap = movieSlots.get(movieName);

                if(listMap.size()>0) {
                    for (Map.Entry<String, MovieTicketBookingDetails> addressSet : listMap.entrySet()) {
                        result.append(" MovieID: ").append(addressSet.getKey()).append("  Movie Name: ").append(movieName).append(" Date: ").append(addressSet.getValue().getMovieDateFromMovieId(addressSet.getKey())).append(" Capacity: ").append(addressSet.getValue().getCapacity()).append(" Available: ").append(addressSet.getValue().getCapacity() - addressSet.getValue().getTotalBooking()).append("\n");

                    }
                }
                else{
                }

            }
            for (String server : Servers){
                logger.info(server + "naam hai");
                if (!ServerName.equals(server)) {

                    MainThread mainThread = new MainThread("ListMovieShowAvailability:" + movieName, getUDPPortNumber(server));
                    Thread thread = new Thread(mainThread);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                    result.append(mainThread.getValue()+"\n");
                    logger.info("received "+ mainThread.getValue() );

                }
            }
            logger.info("return from list movies" + result );
            return result.toString();

        }
        catch (Exception e){
            System.out.println(e);
        }


        return "";
    }


    @Override
    public String AdminListMovieShowsAvailability(String movieName) {

        logger.info("Admin client has request to call"+" AdminListMovieShowsAvailability " + " with data: "+ movieName );

        StringBuilder result = new StringBuilder();

        if (movieSlots.size() > 0) {
            ConcurrentHashMap
                    <String, MovieTicketBookingDetails> listMap = movieSlots.get(movieName);

            // Iterate InnerMap
            for (Map.Entry<String, MovieTicketBookingDetails> addressSet : listMap.entrySet()) {
//                System.out.println(addressSet.getKey());
                result.append(" MovieID: ").append(addressSet.getKey()).append("  Movie Name: ").append(movieName).append(" Date: ").append(addressSet.getValue().getMovieDateFromMovieId(addressSet.getKey())).append(" Capacity: ").append(addressSet.getValue().getCapacity()).append(" Available: ").append(addressSet.getValue().getCapacity() - addressSet.getValue().getTotalBooking()).append("\n");

            }


        }
        logger.info("received "+ result.toString() );
        return result.toString();
    }

    public String getServerBookingSchedule(String customerID) {
        logger.info("client has request to call"+" getServerBookingSchedule " + " with data: "+ customerID );
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, ConcurrentHashMap
                <String, MovieTicketBookingDetails>> empMap : movieSlots.entrySet()) {
            Map<String, MovieTicketBookingDetails> addMap = empMap.getValue();
            for (Map.Entry<String, MovieTicketBookingDetails> addressSet : addMap.entrySet())
                for (Map.Entry<String, Integer> UserDetails : addressSet.getValue().getBookedCustomersIds().entrySet()) {
                    if (UserDetails.getKey().equals(customerID)) {
                        result.append(" ").append(empMap.getKey()).append(" ").append(addressSet.getKey()).append(" ").append(addressSet.getValue().getMovieDateFromMovieId(addressSet.getKey())).append(" ").append(addressSet.getValue().getMovieTimeSlotFromMovieID(addressSet.getKey().charAt(3))).append(" ").append("Tickets You Booked: ").append(addressSet.getValue().getBookedCustomersIds().get(customerID)).append("\n");
                    }
                }
        }
        System.out.println(result);
        logger.info("received "+ result.toString() );
        return result.toString();
    }

    public String serverbookMovieTickets(String customerID, String movieName, String movieID, int numberOfTickets) {
        logger.info(ServerName+ "has received call"+" book movie tickets using udp request " + " with data: "+ customerID+" "+ movieID+" "+ numberOfTickets+" "+movieName );

        String result = "You can't book";
        if (movieSlots.containsKey(movieName)) {
            if (movieSlots.get(movieName).containsKey(movieID)) {
                if (!movieSlots.get(movieName).get(movieID).isHouseFull() && numberOfTickets <= movieSlots.get(movieName).get(movieID).getCapacity() - movieSlots.get(movieName).get(movieID).getTotalBooking() && numberOfTickets <= movieSlots.get(movieName).get(movieID).getCapacity()) {
                    movieSlots.get(movieName).get(movieID).setBookedCustomerIds(customerID, movieSlots.get(movieName).get(movieID).getBookedCustomersIds().getOrDefault(customerID, 0) + numberOfTickets);
                    movieSlots.get(movieName).get(movieID).inTotalBooking(numberOfTickets);

                } else {
                    return result;
                }
            } else {
                return result;
            }
            logger.info("Movie slot booked successfully." );
            return "Movie slot booked successfully.";
        }
        logger.info("received "+ result.toString() );
        return result;
    }

    public String serverCancelMovieTickets(String customerID, String movieName, String movieID, String numberOfTickets) {

        logger.info(ServerName+ "has received request to call"+" serverCancelMovieTickets " + " with data: "+ customerID+" "+ movieID+" "+ numberOfTickets+" "+movieName );

        if (movieSlots.containsKey(movieName)) {
            if (movieSlots.get(movieName).containsKey(movieID)) {
                if (movieSlots.get(movieName).get(movieID).getBookedCustomersIds().containsKey(customerID)) {
                    movieSlots.get(movieName).get(movieID).setBookedCustomerIds(customerID, movieSlots.get(movieName).get(movieID).getBookedCustomersIds().get(customerID) - Integer.parseInt(numberOfTickets));
                    movieSlots.get(movieName).get(movieID).dcTotalBooking(Integer.parseInt(numberOfTickets));
                }
            } else {
                logger.info("Movie Slot not found");
                return "Movie Slot not found";
            }
            logger.info("Movie slot canceled successfully.");
            return "Movie slot canceled successfully.";
        }
        logger.info("Movie Name not found");
        return "Movie Name not found";
    }

    public String serverListMovieShowAvailability(String movieName) {

        logger.info(ServerName+ " has received request to call"+" serverListMovieShowAvailability " + " with data: "+ movieName );

        StringBuilder result = new StringBuilder();

        if (movieSlots.size() > 0) {

            ConcurrentHashMap
                    <String, MovieTicketBookingDetails> listMap = movieSlots.get(movieName);
            for (Map.Entry<String, MovieTicketBookingDetails> addressSet : listMap.entrySet()) {
                System.out.println(addressSet.getKey());
                result.append(" MovieID: ").append(addressSet.getKey()).append("  Movie Name: ").append(movieName).append(" Date: ").append(addressSet.getValue().getMovieDateFromMovieId(addressSet.getKey())).append(" Capacity: ").append(addressSet.getValue().getCapacity()).append(" Available: ").append(addressSet.getValue().getCapacity() - addressSet.getValue().getTotalBooking()).append("\n");
            }

        }
        logger.info("received "+ result.toString() );
        return result.toString();
    }

    public String getTotalBookingForUserForCurrentServer(String customerID) {

        StringBuilder result = new StringBuilder();
        int total = 0;
        System.out.println("Called getTotalBookingForUserForCurrentServer for server : " + ServerName);
        try {
            for (Map.Entry<String, ConcurrentHashMap
                    <String, MovieTicketBookingDetails>> empMap : movieSlots.entrySet()) {
                Map<String, MovieTicketBookingDetails> addMap = empMap.getValue();
//                System.out.println("Check addMap :"+ addMap.toString());
                for (Map.Entry<String, MovieTicketBookingDetails> addressSet : addMap.entrySet()) {
//                    System.out.println("Check addressSet :"+ addressSet.toString());
                    if (addressSet.getValue().getBookedCustomersIds().containsKey(customerID)) {
//                        System.out.println("Found :"+customerID);

                        if (addressSet.getValue().getBookedCustomersIds().get(customerID) > 0) {
//                            System.out.println("Found valid booking");
                            total = total + 1;
                        }

                    }
                }
            }

            result.append(total);
//            System.out.println("get total bookings: " + result);

        } catch (Exception e) {
//            System.out.println("Can't get total bookings, Exception: " + e.getMessage());
            return "0";
        }
        logger.info("received "+ result.toString() );
        return result.toString();
    }

    public String hasCutomerBookedMovieInCurrentServer(String customerID, String movieID, String movieName) {


        String result = "false";
        try {

            System.out.println(movieID + " " + movieName + " " + customerID + " this is debug");

            if (movieSlots.size() > 0) {
                ConcurrentHashMap<String, MovieTicketBookingDetails> ListMap = movieSlots
                        .get(movieName);
                System.out.println("movie details: " + " this is debug: " + movieSlots);
                for (Map.Entry<String, MovieTicketBookingDetails> addressSet : ListMap.entrySet()) {
                    for (Map.Entry<String, Integer> UserDetails : addressSet.getValue().getBookedCustomersIds().entrySet()) {

                        System.out.println("user details: " + " this is debug: " + UserDetails);

                        if (UserDetails.getKey().equals(customerID)) {
                            if (addressSet.getKey().substring(4).equals(movieID.substring(4))) {
                                return "true";
                            } else {
                                result = "false";
                            }
                        } else {
                            result = "false";
                        }
                    }
                }
            }
            return result;

        } catch (Exception e) {
            logger.severe("Exception in hasCustomerBookedMovieInCurrentServer: " + e.getMessage());
            System.out.println("Exception in hasCustomerBookedMovieInCurrentServer: " + e.getMessage());
            return "false";
        }
    }
    static class DateComparator implements Comparator<String> {
        @Override
        public int compare(String d1, String d2) {
            d1 += getTimeFromTimeSlotAbbr(d1.charAt(3));
            d2 += getTimeFromTimeSlotAbbr(d2.charAt(3));
            SimpleDateFormat format = new SimpleDateFormat("ddMMyyHH");
            Date date1;
            try {
                date1 = format.parse(d1.substring(4));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Date date2;
            try {
                date2 = format.parse(d2.substring(4));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return date1.compareTo(date2);
        }

        String getTimeFromTimeSlotAbbr(char abbr) {

            if (abbr == 'M') {
                return "10";
            } else if (abbr == 'A') {
                return "11";
            } else if (abbr == 'E') {
                return "12";
            }
            return "13";
        }

    }


//    Best example
//    public  int x=0;
//
//    public int inc(){
//        System.out.println("called in server"+ ServerName);
//        return ++x;
//    }




    public static void LoggerInitialization(String UserID) {
        FileHandler fh;

        try {
            fh = new FileHandler("./src/Logs/Client/" + UserID + ".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.info("Client " + UserID + " Logger Initialized");

        } catch (SecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


class MainThread implements Runnable {
    String data;
    int port;
    private volatile String value;

    MainThread(String data, int port) {
        this.data = data;
        this.port = port;
    }

    @Override
    public void run() {
        this.value = UDPSendRequest(this.data, this.port);
    }

    public String getValue() {
        return this.value;
    }
}

