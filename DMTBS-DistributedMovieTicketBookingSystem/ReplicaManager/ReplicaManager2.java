package ReplicaManager;

import FinalVariables.FinalVariables;
import Server.AdminImpl;
import Server.Server;
import request.Request;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

public class ReplicaManager2 {

    public static String outputFilePath = System.getProperty("user.dir") + "/RM_DATA/Requests.txt";
    public static String inputFilePath = System.getProperty("user.dir") + "/RM_DATA/Requests.txt";

    static TreeMap<Integer, Request> userRequestList = new TreeMap<>();
    static int lastSuccessfulResponse_CrashFailure = 0;
    static int lastSuccessfulResponse_SWFailure = 0;
    static int failCount = 0;
    static boolean isSWFailed = false;

    public static TreeMap<Integer, Request> getUserRequestList() {
        return userRequestList;
    }

    public static void setUserRequestList(TreeMap<Integer, Request> userRequestList) {
        ReplicaManager2.userRequestList = userRequestList;
    }

    public int getLastSuccessfulResponse_CrashFailure() {
        return lastSuccessfulResponse_CrashFailure;
    }

    public int getLastSuccessfulResponse_SWFailure() {
        return lastSuccessfulResponse_SWFailure;
    }

    public static Server server ;

    static int expectedSequencerId;
    public ReplicaManager2() {
        server=new Server();
        expectedSequencerId = 1;
        System.out.println("Hashmap for userRequestList : " + ReplicaManager2.userRequestList.toString());
//        try {
////            readFromFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public void readFromFile() throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            for (String line; (line = br.readLine()) != null;) {
                String[] keyValue = line.trim().split(":");
                //ReplicaManager.userRequestList.put(Integer.parseInt(keyValue[0].trim()), keyValue[1].trim());
            }

            System.out.println("Hashmap after writing from file : " + ReplicaManager2.userRequestList.toString());
        }
    }

    private void writeToFile() throws IOException {
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(outputFilePath));
            for (Map.Entry<Integer, Request> entry : ReplicaManager2.userRequestList.entrySet()) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bf.close();
        }
    }


    private void processRequest(Request request) throws IOException {

        AdminImpl subserver=null;
        if(request.getUserServer().equals("Atwater")){
            subserver=server.Atwater;

        } else if (request.getUserServer().equals("Outremount")) {
            subserver=server.Outremont;
        }
        else if(request.getUserServer().equals("Verdun")){
            subserver=server.Verdun;
        }
        request.setRMId("RM2");
        switch (request.getFunctionName()) {

            case "bookMovieTickets":

                request.setRM1response(subserver.bookMovieTickets(request.getCustomerID(),
                        request.getMovieID(),
                        request.getMovieName(),
                        request.getNumberOfTickets()));
                break;

            case "cancelMovieTickets":
                request.setRM1response(subserver.cancelMovieTickets(request.getCustomerID(),
                        request.getMovieID(),
                        request.getMovieName(),
                        String.valueOf(request.getNumberOfTickets())));
                break;
            case "exchangeTickets":
                request.setRM1response(subserver.exchangeTickets(request.getCustomerID(),
                        request.getMovieID(),request.getMovieName(), request.getNew_movieID(),request.getNew_movieName(),
                        request.getNumberOfTickets()));
                break;
            case "addMovieSlots":
                request.setRM1response(subserver.addMovieSlots(request.getMovieID(),
                        request.getMovieName(),request.getCapacity()));
                break;
            case "removeMovieSlots":
                request.setRM1response(subserver.removeMovieSlots(request.getMovieID(),
                        request.getMovieName()));
                break;
            case "listMovieShowsAvailability":
                request.setRM1response(subserver.listMovieShowsAvailability(request.getMovieName()));
                break;
            case "getBookingSchedule":
                request.setRM1response(subserver.getBookingSchedule(request.getCustomerID()));
                break;


        }


        if(request.getRequestId()<expectedSequencerId && isSWFailed){
         System.out.println("Processed backlog request" + request.getRequestId());
        }
        else {
            DatagramSocket socket = new DatagramSocket(FinalVariables.RM_SENDER);

            byte[] serializedMessage = getSerializedObject(request);

            // Send Object Back to frontend

            SendObjectToFrontend(socket, serializedMessage);


            expectedSequencerId++;
            socket.close();
        }

    }

    private static void SendObjectToFrontend(DatagramSocket socket, byte[] serializedMessage) throws IOException {
        DatagramPacket rtsequencer = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(FinalVariables.SYSTEM_IP_ADDRESS), 1082);
        socket.send(rtsequencer);
        System.out.println("sent response to frontend");
    }

    private static byte[] getSerializedObject(Request request) throws IOException {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(request);
        byte[] serializedMessage = bStream.toByteArray();
        return serializedMessage;
    }


    private void listenForRequestFromSequencer() throws ParseException, InterruptedException, FileNotFoundException, IOException {

        try {
            MulticastSocket listenerSocket = new MulticastSocket(3341);
            InetAddress group = InetAddress.getByName("224.0.0.0");
            listenerSocket.joinGroup(group);
            System.out.println("Multicast  Group Joined");
            byte[] bArray = new byte[1024];
            while (true){
                try {
                    DatagramPacket packet = new DatagramPacket(bArray, bArray.length);
                    listenerSocket.receive(packet);
                    byte[] data = packet.getData();
                    ByteArrayInputStream in = new ByteArrayInputStream(data);
                    ObjectInputStream is = new ObjectInputStream(in);
                    Request request = (Request) is.readObject();
                    System.out.println("request object received = " + request);
                    // introduce a bug
//                    if(expectedSequencerId >= 2 && expectedSequencerId <=4) {
//                        request.setNumberOfTickets(100000);
//                        System.out.println("Introducing bug intentionally in request 2, 3 and 4 for SW failure");
//                        System.out.println("Updated request object  = " + request);
//                    }
                    userRequestList.put(request.getRequestId(),request);
                    processRequest(request);

                } catch (Throwable var8) {
                   System.out.println(var8);
                }
        }


        } catch (IOException var9) {
            System.out.println("Inside catch - " + String.valueOf(var9));
        }


    }
    public static void main(String[] args) {

            ReplicaManager2 replicaManager = new ReplicaManager2();

            //listenForRequestFromSequencer
            Runnable taskOfReplicaManager = () -> {
                try {
                    replicaManager.listenForRequestFromSequencer();
                } catch (ParseException | InterruptedException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            };
            Thread threadOfReplicaManager = new Thread(taskOfReplicaManager);
            threadOfReplicaManager.start();

        Runnable failureListener = () -> {
            try {
                replicaManager.listenForSWFailure();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
        Thread threadOfReplicaManager2 = new Thread(failureListener);
        threadOfReplicaManager2.start();

    }

    private void listenForSWFailure() throws IOException {
        DatagramSocket socket = new DatagramSocket(FinalVariables.RM_LISTENER_PORT_FOR_SOFTWARE_FAILURE);
        byte[] incomingData = new byte[1024];

        while (true) {
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            socket.receive(incomingPacket);
            byte[] data = incomingPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            try {
                Request request = (Request) is.readObject();
                System.out.println("request object received = " + request);
                if(request.isSWFail()==true)
                    failCount++;
                System.out.println("Current fail count :"+ failCount);
                if(failCount >= 3) {
                    isSWFailed = true;
                    System.out.println("Starting Recovery");
                    Thread.sleep(10000);
                    server.brahmastra();
                    for (Map.Entry<Integer, Request> req : ReplicaManager2.userRequestList.entrySet()) {
                        processRequest(req.getValue());
                    }
                    System.out.println( "Recovery Completed");
                    failCount = 0;
                    isSWFailed = false;
                    System.out.println("Current fail count :"+ failCount);
                }

            }catch(Exception e){
                System.out.println("Ye fat chuka hai : " + e);
            }
        }
    }


}
