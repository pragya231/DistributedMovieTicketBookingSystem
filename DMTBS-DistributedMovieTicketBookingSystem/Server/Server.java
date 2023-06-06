package Server;

import javax.xml.ws.Endpoint;

import Server.AdminImpl;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class Server {


    /*
    ######################################################################################
     */

    public  AdminImpl Atwater;
    public  AdminImpl Verdun;
    public  AdminImpl Outremont;

    /*
    ######################################################################################
     */
    public static final int ATW_portNum = 3333;
    public static final int VER_portNum = 3334;
    public static final int OUT_portNum = 3335;

    public static final int ATW_UDP_portNum = 3336;
    public static final int VER_UDP_portNum = 3337;
    public static final int OUT_UDP_portNum = 3338;

    /*
    #######################################################################################
     */

    // Constructor

    public Server(){
        Atwater= new AdminImpl("atwater");
        Verdun= new AdminImpl("verdun");
        Outremont= new AdminImpl("outremont");

        new Thread(() -> UdpServer(Atwater, "atwater")).start();
        new Thread(() -> UdpServer(Verdun, "verdun")).start();
        new Thread(() -> UdpServer(Outremont, "outremont")).start();
//        this.StartServer(Atwater,"atwater");
//        this.StartServer(Verdun,"verdun");
//        this.StartServer(Outremont,"outremont");
    }

    /*
    #######################################################################################
     */
    public static int getPortNumber(String serv_name) {
        if(serv_name.equals("atwater"))
            return ATW_portNum;
        if(serv_name.equals("verdun"))
            return VER_portNum;
        if(serv_name.equals("outremont"))
            return OUT_portNum;
        return -1;
    }

    public static int getUDPPortNumber(String serv_name ) {
        if(serv_name.equals("atwater"))
            return ATW_UDP_portNum;
        if(serv_name.equals("verdun"))
            return VER_UDP_portNum;
        if(serv_name.equals("outremont"))
            return OUT_UDP_portNum;
        return -1;
    }


    /*
    ########################################BHRAMASTRA#####################################################
     */



    private static void StartServer(AdminImpl server,String serv_name) {
        try {



            new Thread(() -> UdpServer(server, serv_name)).start();


//            Endpoint ep=Endpoint.publish("http://localhost:"+getPortNumber(serv_name)+"/DMTBS", server);
            // Endpoint ep=Endpoint.publish("http://localhost:/DMTBS", server);
//            System.out.println(ep.isPublished());


        } // end try
        catch (Exception re) {
            System.out.println("Exception in Server.main: " + re);
        } // end catch
    }

    public static void UdpServer(AdminImpl Server,String serverName){
        System.out.println(serverName + "running.,...");
        while (true) {
            try (DatagramSocket aSocket = new DatagramSocket(getUDPPortNumber(serverName))) {
                String result = "";
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String data = new String(request.getData()).trim();
//                System.out.println(data);
                String[] data1=data.split(":");

                System.out.println("data1[0]"+ data1[0]);
//                System.out.println(Server);

                switch (data1[0])
                {
                    case "getBookingSchedule":

                        result=Server.getServerBookingSchedule(data1[1]);
                        break;
                    case "bookMovieTickets":
                        String[] para=data1[1].split(" ");
                        result=Server.serverbookMovieTickets(para[0],para[1],para[2],Integer.parseInt(para[3]));
                        break;
                    case "getTotalBookingForUserForCurrentServer":
                        String[] para2=data1[1].split(" ");
                        result=Server.getTotalBookingForUserForCurrentServer(para2[0]);
                        System.out.println(result);
                        break;
                    case "hasCutomerBookedMovieInCurrentServer":
                        String[] para3=data1[1].split(" ");
                        System.out.println("going to call hasCutomerBookedMovieInCurrentServer for "+Server);
                        result=Server.hasCutomerBookedMovieInCurrentServer(para3[0],para3[1],para3[2]);
                        System.out.println("aa gaya");
                        break;
                    case "cancelMovieTickets":
                        String[] para1=data1[1].split(" ");
                        System.out.println(para1[0]+para1[1]+para1[2]+Integer.parseInt(para1[3]));
                        result=Server.serverCancelMovieTickets(para1[0],para1[2],para1[1],para1[3]);
                        System.out.println("result"+result);
                        break;
                    case "ListMovieShowAvailability":
                        System.out.println("--> "+ data1[1]);
                        result=Server.serverListMovieShowAvailability(data1[1]);
                        System.out.println("result"+result);
                        break;
                }


                DatagramPacket reply = new DatagramPacket(result.getBytes(), result.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
                System.out.println("Sent the reply to "+request.getAddress()+"  "+request.getPort());
                aSocket.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }



    public void brahmastra(){
        System.out.println("brahmastra : >-----------------------> sssuuuuuuuuuuuiiiiiiiiiiiiiiiiiinnnnn");
        System.out.println("Replica ka punar-janam");
        Atwater= new AdminImpl("atwater");
        Verdun=new AdminImpl("verdun");
        Outremont= new AdminImpl("outremont");
    }

}
