package sequencer;
import FinalVariables.FinalVariables;
import request.Request;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class sequencer {
    static int sequenceId=0;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket(FinalVariables.SEQUENCER_PORT);
        byte[] incomingData = new byte[1024];

        while (true) {
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            socket.receive(incomingPacket);
            byte[] data = incomingPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            try {
                Request request = (Request) is.readObject();
                request.setRequestId(++sequenceId);
                System.out.println("request object received = " + request);


                // get serialized object
                byte[] serializedMessage = getSerializedObject(request);

                // Send Object Back to frontend

                SendObjectToFrontend(socket, serializedMessage);

                try{
                    Thread.sleep(2000);
                }catch (Exception e){

                }
                // Sending Object to replicas
                SendObjectToReplicas(socket, serializedMessage);


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }



        }
    }

    private static void SendObjectToReplicas(DatagramSocket socket, byte[] serializedMessage) throws IOException {
        InetAddress group = InetAddress.getByName("224.0.0.0");
        DatagramPacket packet;
        packet = new DatagramPacket(serializedMessage, serializedMessage.length, group, FinalVariables.RM_LISTENER_PORT_FROM_SEQUENCER);
        socket.send(packet);
        System.out.println("send to replica");
    }

    private static void SendObjectToFrontend(DatagramSocket socket, byte[] serializedMessage) throws IOException {
        DatagramPacket rtsequencer = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(FinalVariables.SYSTEM_IP_ADDRESS), FinalVariables.SEQUENCER_TO_FRONT_PORT);
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
}
