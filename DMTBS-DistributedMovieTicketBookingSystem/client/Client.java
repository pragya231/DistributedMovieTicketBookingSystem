package client;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import Server.CheckData;
import Server.Logger;
import frontend.FrontEndInterface;

import static FinalVariables.FinalVariables.FRONT_PORT_WS;
import static FinalVariables.FinalVariables.SYSTEM_IP_ADDRESS;

public class Client {
  String userID;
  boolean isAdmin;
  String serv_ID;

  public static enum movie {
    AVENGERS,
    TITANIC,
    AVATAR
  };
  public static LocalDate currentdate = LocalDate.now();
  /**
   * constructor to assign user category and admin or not
   * @param userName
   */
  Client(String userName) {

    this.userID = userName;
    char c = userName.charAt(3);
    if (c == 'A')
      this.isAdmin = true;
    else if (c == 'P')
      this.isAdmin = false;
    this.serv_ID = userName.substring(0, 3).toUpperCase();
  }

  /**
   * it returns the server name
   * @param serv_ID
   * @return
   */
  public static String get_Server_Name(String serv_ID) {
    if (serv_ID.equals("ATW"))
      return "ATWATER";
    else if (serv_ID.equals("VER"))
      return "VERDUN";
    else if (serv_ID.equals("OUT"))
      return "OUTREMONT";
    else {
      System.out.println("Unable to identify Server Name");
      return "Unknown";
    }

  }

  public static void main(String[] args) throws NotBoundException, IOException, ExecutionException, InterruptedException {
    System.out.println("\t\t\t\t\t<<Shivamplex>>");

    System.out.println("Date : " + currentdate.getDayOfMonth() + " " + currentdate.getMonth() + " " +
      currentdate.getYear());

    for (int i = 0; i < 150; i++)
      System.out.print("*");
    System.out.println("\n\n>Enter User Name : ");
    Scanner scanner = new Scanner(System.in);
    String s = scanner.nextLine().trim().toUpperCase();
    while (!CheckData.checkUserName(s)) {
      System.out.println(">Invalid User Name");
      System.out.println(">Enter User Name : ");
      s = scanner.nextLine().trim().toUpperCase();
    }
    

    Client user = new Client(s);
    System.out.println("Logged in with user : " + user.userID + " on server " + user.serv_ID);
    System.out.print("[Is Admin : " + user.isAdmin + "]\n");
    Logger.enterLog(user.userID, (String)(user.userID + " connected to server " + user.serv_ID));

    String url_address = "http://localhost:8080/project?wsdl";
    String s1 = "http://"+SYSTEM_IP_ADDRESS+":"+FRONT_PORT_WS+"/FrontEnd?wsdl";
    System.out.println(s1);
    URL urlServerService = new URL(s1);


    QName qNameServerService= new QName("http://frontend/","FrontEndImplementationService");
    Service service = Service.create(urlServerService, qNameServerService);
    FrontEndInterface feService = service.getPort(frontend.FrontEndInterface.class);

    String options[] = {
      "1 : Add a movie slot",
      "2 : Remove a movie slot",
      "3 : Check movie availability",
      "4 : Book a movie ticket",
      "5 : Get movie Schedule",
      "6 : Cancel movie ticket",
      "7 : Exchange ticket",
      "8 : exit"
    };

    while(true) {
      System.out.println("\nPlease select one of the actions : ");

      //Display options to users
      if (user.isAdmin)
        System.out.println(options[0] + "\n" + options[1] + "\n" + options[2] + "\n" + options[3] + "\n" + options[4] + "\n" + options[5] + "\n" + options[6] + "\n" + options[7]);
      else
        System.out.println(options[3] + "\n" + options[4] + "\n" + options[5] + "\n" + options[7]);

      int selVal = Integer.parseInt(scanner.nextLine());
      //			System.out.println("User Requested Option : " + options[selVal-1]);
      //			Logger.enterLog(user.userName,(String)("User Requested Option : " + options[selVal-1]));
      int response;
      String responsefromFrontend;
      if (user.isAdmin && selVal == 1) {
        int capacity;
        response = 1;
        String movieID, movieName, dt, slot;

        System.out.println("Select movie name from below list: ");
        for (movie m: movie.values())
          System.out.print(m + "   ");
        System.out.println("");
        movieName = scanner.nextLine().trim().toUpperCase();
        boolean movieExist = false;
        for (movie m: movie.values())
          if (movieName.equalsIgnoreCase(m.toString())) {
            movieExist = true;
            break;
          }
        if (!movieExist) {
          System.out.println("Movie - " + movieName + " does not exist");
          continue;
        }

        System.out.println("Enter the date in  this format[DDMMYY]");
        dt = scanner.nextLine().trim();
        if (!CheckData.checkDateFormat(dt)) {
          System.out.println("Date - " + dt + " is Invalid");
          continue;
        }
        if (!CheckData.checkDateLimit(dt)) {
          System.out.println("Date - " + dt + " is not in scope as of now. You can add slot from today till " +
            (currentdate.getDayOfMonth() + 7) + " " + currentdate.getMonth() + " " +
            currentdate.getYear());
          continue;
        }
        System.out.println("Enter the slot [M - Morning, A - Afternoon, E - Evening]");
        slot = scanner.nextLine().trim().toUpperCase();
        if (slot.equals("A") | slot.equals("M") | slot.equals("E")) {
          movieID = user.serv_ID + slot + dt;
        } else {
          System.out.println("Slot - " + slot + " is Invalid");
          continue;
        }

        System.out.println("Enter Capacity : ");
        capacity = Integer.parseInt(scanner.nextLine());
        Logger.enterLog(user.userID, (String)("Add an movie : User " + user.userID + " requested addition of movie slot of " + movieName + " with movie id " + movieID + " with capacity " + capacity));
        responsefromFrontend = feService.addMovieSlots(user.userID,movieID, movieName, capacity);
        if (responsefromFrontend.equals(null)) {
          System.out.println("Add a movie  : Failed!! movie  " + movieName + " can not be addded. [out of scope]");
          Logger.enterLog(user.userID, (String)("Add a movie  : Failed!! movie  " + movieName + " can not be addded. [out of scope]"));
        } else {
          System.out.println(responsefromFrontend+" " + movieName + " with movie id " + movieID + " with capacity " + capacity);
          Logger.enterLog(user.userID, (String)("Add a movie  : Success!! Added movie type " + movieName + " with movie id " + movieID + " with capacity " + capacity));
        }
      } else if (user.isAdmin && selVal == 2) {
        int result = 0;
        String movieName, movieID;
        System.out.println("Select movie name from below list: ");
        for (movie m: movie.values())
          System.out.print(m + "   ");
        System.out.println("");
        movieName = scanner.nextLine().trim().toUpperCase();
        boolean movieExist = false;
        for (movie m: movie.values())
          if (movieName.equalsIgnoreCase(m.toString())) {
            movieExist = true;
            break;
          }
        if (!movieExist) {
          System.out.println("Movie - " + movieName + " does not exist");
          continue;
        }
        System.out.println("Enter movie ID in format Location + Slot + Date[DDMMYY] ex: ATWA120223");
        movieID = scanner.nextLine();

        Logger.enterLog(user.userID, (String)("Remove movie  : Admin " + user.userID + " requested to check availability of movie name " + movieName));
        responsefromFrontend = feService.removeMovieSlots(user.userID,movieID, movieName);
        if (responsefromFrontend.equals(null)){
          System.out.println("Remove movie slots fails");
        }
        else{
          System.out.println(responsefromFrontend+" " + movieName + " and movie ID " + movieID);
          Logger.enterLog(user.userID, (String)("removeMovieSlots  : Success!! Removed movieName " + movieName + " and movie ID " + movieID));

        }

      } else if (user.isAdmin && selVal == 3) {
        String result = "";
        String movieName;
        //scanner.nextLine();
        System.out.println("Select movie name from below list: ");
        for (movie m: movie.values())
          System.out.print(m + "   ");
        System.out.println("");
        movieName = scanner.nextLine().trim().toUpperCase();
        boolean movieExist = false;
        for (movie m: movie.values())
          if (movieName.equalsIgnoreCase(m.toString())) {
            movieExist = true;
            break;
          }
        if (!movieExist) {
          System.out.println("Movie - " + movieName + " does not exist");
          continue;
        }
        Logger.enterLog(user.userID, (String)("list Availability : User " + user.userID + " requested to check availability of movie name " + movieName));
        responsefromFrontend = feService.listMovieShowsAvailability(user.userID,movieName);
        System.out.println(responsefromFrontend);
        Logger.enterLog(user.userID, "Result of the query as below :");
        Logger.enterLog(user.userID, result);
      } else if (selVal == 4) {

        int numberOfTickets = 0;
        String customerID, movieID, movieName, dt, slot, loc;

        customerID = user.userID;

        System.out.println("Select movie name from below list: ");
        for (movie m: movie.values())
          System.out.print(m + "   ");
        System.out.println(">");
        movieName = scanner.nextLine().trim().toUpperCase();
        boolean movieExist = false;
        for (movie m: movie.values())
          if (movieName.equalsIgnoreCase(m.toString())) {
            movieExist = true;
            break;
          }
        if (!movieExist) {
          System.out.println("Movie - " + movieName + " does not exist");
          continue;
        }
        System.out.println("Enter the date in  this format[DDMMYY]");
        dt = scanner.nextLine().trim();
        if (!CheckData.checkDateFormat(dt)) {
          System.out.println("Date - " + dt + " is Invalid");
          continue;
        }
        if (!CheckData.checkDateLimit(dt)) {
          System.out.println("Date - " + dt + " is not in scope as of now. You can add slot till " +
            (currentdate.getDayOfMonth() + 7) + " " + currentdate.getMonth() + " " +
            currentdate.getYear());
          continue;
        }
        System.out.println("Select location from ATW - Atwater, VER - Verdun, OUT - Outremont");
        loc = scanner.nextLine().trim().toUpperCase();
        if (!(loc.equals("ATW") | loc.equals("VER") | loc.equals("OUT"))) {
          System.out.println("Location - " + loc + " is Invalid");
          continue;
        }
        System.out.println("Enter the slot [M - Morning, A - Afternoon, E - Evening]");
        slot = scanner.nextLine().trim().toUpperCase();
        if (slot.equals("A") | slot.equals("M") | slot.equals("E")) {
          movieID = loc + slot + dt;
        } else {
          System.out.println("Slot - " + slot + " is Invalid");
          continue;
        }

        System.out.println("Enter no of tickets : ");
        numberOfTickets = Integer.parseInt(scanner.nextLine());
        Logger.enterLog(user.userID, (String)("Book an movie  : User " + user.userID + " requested booking of movie type " + movieName + " with movie id " + movieID + " for Patient " + customerID));
        responsefromFrontend = feService.bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
       if(responsefromFrontend.equals(null)){
         System.out.println("Process failed!!!"+ responsefromFrontend);
       } else {
         System.out.println("Process success!!!"+ responsefromFrontend);
        }
      } else if (selVal == 5) {
        String response1 = "";
        String customerID;
        customerID = user.userID;
        Logger.enterLog(user.userID, (String)("getmovieSchedule : User " + user.userID + " requested schedules of movie for " + customerID));
        response1 = feService.getBookingSchedule(customerID);
        Logger.enterLog(user.userID, (String)("getmovieSchedule output for " + customerID + " in all servers is \n " + response1));
        //System.out.println("Movie Name      Movie id                 No of Tickets");
        System.out.println( response1);
      } else if (selVal == 6) {

        int numberOfTickets = 0;
        String movieID, movieName = "";
        System.out.println("Enter movie ID in format Location + Slot + Date ex: ATWA121022");
        movieID = scanner.nextLine();
        System.out.println("Enter movie name :");
        movieName = scanner.nextLine();
        System.out.println("Enter number of tickets :");
        numberOfTickets = Integer.parseInt(scanner.nextLine());
        Logger.enterLog(user.userID, (String)("Cancel a booking : User " + user.userID + " requested cancel of movie id " + movieID));
        responsefromFrontend = feService.cancelMovieTickets(user.userID, movieID, movieName, Integer.parseInt(numberOfTickets + ""));

        if(!responsefromFrontend.equals(null)){
          System.out.println("Cancel an movie : Success!! " + user.userID + " cancelled " + numberOfTickets + " tickets with movie id " + movieID);
          Logger.enterLog(user.userID, (String)("Cancel a booking : Success!! " + user.userID + " cancelled all movies with id " + movieID));
        }
        else{
          System.out.println("can't Cancel an movie : failed!! " + user.userID + " cancelled " + numberOfTickets + " tickets with movie id " + movieID);
          Logger.enterLog(user.userID, (String)("can't Cancel an movie : failed!! " + user.userID + " cancelled all movies with id " + movieID));

        }
      } else if (selVal == 7) {

          int numberOfTickets = 0;
          String customerID, old_movieID, new_movieID, old_movieName, new_movieName;
          customerID = user.userID;
          
          System.out.println("Enter movie name you want to cancel : ");
          old_movieName = scanner.nextLine().trim().toUpperCase();
          boolean movieExist = false;
          for (movie m: movie.values())
            if (old_movieName.equalsIgnoreCase(m.toString())) {
              movieExist = true;
              break;
            }
          if (!movieExist) {
            System.out.println("Movie - " + old_movieName + " does not exist");
            continue;
          }
          
          System.out.println("Enter movie name you want to book : ");
          new_movieName = scanner.nextLine().trim().toUpperCase();
           movieExist = false;
          for (movie m: movie.values())
            if (new_movieName.equalsIgnoreCase(m.toString())) {
              movieExist = true;
              break;
            }
          if (!movieExist) {
            System.out.println("Movie - " + new_movieName + " does not exist");
            continue;
          }
          
          System.out.println("Enter movie id you want to cancel : ");
          old_movieID = scanner.nextLine().trim().toUpperCase();
          
          System.out.println("Enter new movie id you want to book : ");
          new_movieID = scanner.nextLine().trim().toUpperCase();
          
          System.out.println("Enter no of ticktes you want to exchange : ");
          numberOfTickets = Integer.parseInt(scanner.nextLine());;

        responsefromFrontend = feService.exchangeTickets(customerID, old_movieID, old_movieName, new_movieID, new_movieName, numberOfTickets);
        
        if(responsefromFrontend.equals(null)) {
        	System.out.println("Could not complete exchange tickets.");
        }
        else {
        	System.out.println(responsefromFrontend+ " success !!");
        }

      } else if (selVal == 8) {
    	  scanner.close();
        System.exit(0);
      } else
        System.out.println("Invalid Input or operation not Allowed for " + user.userID);


    }
  }
}