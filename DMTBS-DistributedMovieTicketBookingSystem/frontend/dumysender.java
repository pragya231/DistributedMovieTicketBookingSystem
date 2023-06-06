package frontend;

import request.Request;
import FinalVariables.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;

public class dumysender {

    private void listenForResponseFromRM() throws ParseException, InterruptedException, FileNotFoundException, IOException {

        DatagramSocket socket = new DatagramSocket(FinalVariables.RM_SENDER_PORT_TO_FE);
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
                if(request.getRequestId() >= 2 && request.getRequestId() <=4) {
                    request.setSWFail(true);
                    failureNotification(request);
                }


//

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }



        }

    }

    private void sender(Request request){
        try{




            // Serialize to a byte array
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(request);
            byte[] serializedMessage = bStream.toByteArray();
            DatagramSocket datagramSocketfe = new DatagramSocket(FinalVariables.FRONT_PORT);
            DatagramPacket rtsequencer = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(FinalVariables.SYSTEM_IP_ADDRESS), FinalVariables.SEQUENCER_PORT);
            datagramSocketfe.send(rtsequencer);
            //  datagramSocketfe.send(rtsequencer);

            System.out.println("sent response to Sequencer");
            datagramSocketfe.close();



        } catch(Exception e){
            System.out.println("Fata :"+e);
        }
        System.out.println("Multicast  Time Client Terminated");




}

    private static void failureNotification(Request request){
        System.out.println("Multicast  Time Client");
        try{




            // Serialize to a byte array
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(request);
            byte[] serializedMessage = bStream.toByteArray();
            DatagramSocket datagramSocketfe = new DatagramSocket(FinalVariables.FRONT_PORT_Failure_Port);
            DatagramPacket rtsequencer = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(FinalVariables.SYSTEM_IP_ADDRESS), FinalVariables.RM_LISTENER_PORT_FOR_SOFTWARE_FAILURE);
            datagramSocketfe.send(rtsequencer);
            //  datagramSocketfe.send(rtsequencer);

            System.out.println("sent response to Sequencer");
            datagramSocketfe.close();



        } catch(Exception e){
            System.out.println("Fata :"+e);
        }
        System.out.println("Multicast  Time Client Terminated");


    }

    public static void main(String[] args) throws InterruptedException {
        dumysender d = new dumysender();
        Runnable taskOfReplicaManager = () -> {
            try {
                System.out.println("Y");
                d.listenForResponseFromRM();
            } catch (ParseException | InterruptedException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
        Thread threadOfReplicaManager = new Thread(taskOfReplicaManager);
        threadOfReplicaManager.start();
        Request request = new Request();
        d.sender(request);
        d.sender(request);
        d.sender(request);
        d.sender(request);
        Thread.sleep(2000);
        d.sender(request);
//        Runnable taskOfReplicaManager1 = () -> {
//            try {
//                System.out.println("X");
//                d.sender(request);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        };
//        Thread threadOfReplicaManager1 = new Thread(taskOfReplicaManager1);
//        threadOfReplicaManager1.start();
        while(true){}

      //

    }
}
