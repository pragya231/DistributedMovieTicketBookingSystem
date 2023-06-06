package frontend;

import request.Request;

import javax.xml.ws.Endpoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.HashMap;

import static FinalVariables.FinalVariables.*;
import static frontend.FrontEndImplementation.AllRequest;


public class FrontEndPublisher {
	static HashMap<Integer,Answer> map = new HashMap<Integer,Answer>();
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//    	FrontEndImplementation fei = new FrontEndImplementation();
		Endpoint ep = Endpoint.create(new FrontEndImplementation());

		ep.publish("http://"+SYSTEM_IP_ADDRESS+":"+FRONT_PORT_WS+"/FrontEnd");
		System.out.println("Front end webservice is published: " + ep.isPublished());
		frontEndReceiver();
	}

	public static void frontEndReceiver() throws IOException, ClassNotFoundException {

		DatagramSocket socket;
		try {
			socket = new DatagramSocket(1082, InetAddress.getByName(SYSTEM_IP_ADDRESS));

			byte[] buffer = new byte[1000];
			while(true) {
				System.out.println("waiting for message");
				DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(incomingPacket);

				String rec_Msg =  new String(incomingPacket.getData());

				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				Request requestrecived = (Request) is.readObject();

				System.out.println("reponse"+ requestrecived.toString());

				if(AllRequest.containsKey(requestrecived.getRequestId())){
					if(requestrecived.getRMId().equals("RM1")){
					AllRequest.get(requestrecived.getRequestId()).setRM1response(requestrecived.getRM1response());
					}
					if(requestrecived.getRMId().equals("RM2")){
						AllRequest.get(requestrecived.getRequestId()).setRM2response(requestrecived.getRM2response());
					}
					if(requestrecived.getRMId().equals("RM3")){
						AllRequest.get(requestrecived.getRequestId()).setRM3response(requestrecived.getRM3response());
					}
				}

				System.out.println(AllRequest);


			}
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class Answer{
	String r1=null;
	String r2=null;
	String r3=null;
}