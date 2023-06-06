package frontend;

import FinalVariables.FinalVariables;
import request.Request;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService(endpointInterface = "frontend.FrontEndInterface")
@SOAPBinding(style = Style.RPC)
public class FrontEndImplementation implements FrontEndInterface{
	public static final int SEQ_Udp_Port = 1566;
	public static ConcurrentHashMap<Integer,Request> AllRequest = new ConcurrentHashMap<Integer,Request>();
	static long longestResponseTime = 2;
	static int rCount = 0;
	private int sender(Request request){
		try{
			// Serialize to a byte array
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutput oo = new ObjectOutputStream(bStream);
			oo.writeObject(request);
			byte[] serializedMessage = bStream.toByteArray();
			DatagramSocket datagramSocketfe = new DatagramSocket(FinalVariables.FRONT_PORT);
			DatagramPacket rtsequencer = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(FinalVariables.SYSTEM_IP_ADDRESS), FinalVariables.SEQUENCER_PORT);
			datagramSocketfe.send(rtsequencer);
			datagramSocketfe.close();
			System.out.println("sent response to Sequencer");
			DatagramSocket socket = new DatagramSocket(FinalVariables.SEQUENCER_TO_FRONT_PORT);

			byte[] incomingData = new byte[1024];
			System.out.println("sendUDP :  after sending request");

			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			socket.receive(incomingPacket);
			byte[] data = incomingPacket.getData();
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(in);

			Request requestrecived = (Request) is.readObject();
			System.out.println("request object received from sequencer = " + requestrecived);
//			request.setRequestId(++rCount);
			AllRequest.put(requestrecived.getRequestId(),requestrecived);
//			datagramSocketfe.close();
			socket.close();

			return requestrecived.getRequestId();
		} catch(Exception e){
			System.out.println("Fata :"+e);
		}
		System.out.println("Multicast  Time Client Terminated");

		return 0;
	}


	public String addMovieSlots (String UserID, String movieID, String movieName, int bookingCapacity) throws IOException, ExecutionException, InterruptedException {

		String UserServer = getUserServer(UserID);

		Request request = new Request(UserServer,"addMovieSlots",bookingCapacity,movieID,movieName,"","","",0);


		int requiredid = sender(request);

		Thread.sleep(6000);

		return CheckSoftwareFailureAndSendResponse(requiredid);

	}

	private static String CheckSoftwareFailureAndSendResponse(int requiredid) throws InterruptedException {
		int counter=0;
		while(true){
			counter++;
			Thread.sleep(2000);
			if(AllRequest.get(requiredid).getRM1response().equals(null) && AllRequest.get(requiredid).getRM2response().equals(null) && AllRequest.get(requiredid).getRM3response().equals(null) && counter<3){
				if(counter==0){
					Thread.sleep(2000);
				}
				else if(counter==1){
					Thread.sleep(4000);
				}
				else{

					if(AllRequest.get(requiredid).getRM1response().equals(null)){

						AllRequest.get(requiredid).setSWFail(true);

						failureNotification(AllRequest.get(requiredid),"RM1");
					}
					else if(AllRequest.get(requiredid).getRM2response().equals(null)){
						AllRequest.get(requiredid).setSWFail(true);

						failureNotification(AllRequest.get(requiredid),"RM2");
					}
					else if(AllRequest.get(requiredid).getRM1response().equals(null)){
						AllRequest.get(requiredid).setSWFail(true);

						failureNotification(AllRequest.get(requiredid),"RM3");
					}

				}

			}
			else{



				return Majority(requiredid);

			}
		}
	}

	private static String Majority(int requiredid) {
		String rm1Response = AllRequest.get(requiredid).getRM1response();
		String rm2Response = AllRequest.get(requiredid).getRM2response();
		String rm3Response = AllRequest.get(requiredid).getRM3response();

		int count1 = 0, count2 = 0, count3 = 0;

		if(rm1Response!=null){
		if (rm1Response.equals(rm2Response))
			count1 = count2 = 1;
		}

		if(rm2Response!=null) {
			if (rm1Response.equals(rm3Response))
				count1 = count3 = 1;
		}

		if(rm2Response!=null) {
			if (rm2Response.equals(rm3Response))
				count2 = count3 = 1;
		}

		if(count1==count2 && count2== count3){
//			AllRequest.get(requiredid).setSWFail(true);
//			failureNotification(AllRequest.get(requiredid),"RM2");
			return rm1Response;
		} else if ((count1 == count2 && count1 !=count3) || (count2 == count1 && count2 !=count3)) {
			AllRequest.get(requiredid).setSWFail(true);
			failureNotification(AllRequest.get(requiredid),"RM3");
			return rm1Response;
		}
		else if ((count1 == count3 && count1 !=count2) || (count3 == count1 && count2 !=count3)) {
			AllRequest.get(requiredid).setSWFail(true);
			failureNotification(AllRequest.get(requiredid),"RM2");
			return rm3Response;
		}else{
			AllRequest.get(requiredid).setSWFail(true);
			failureNotification(AllRequest.get(requiredid),"RM1");
			return rm2Response;
		}


	}


	private static void failureNotification(Request request,String RM){
		System.out.println("Multicast  Time Client");
		try{
			String IP;
			if(RM.equals("RM1")){
				IP=FinalVariables.RM1_IP_ADDRESS;
			}
			else if(RM.equals("RM2")){
				IP=FinalVariables.RM2_IP_ADDRESS;
			}
			else {
				IP=FinalVariables.RM3_IP_ADDRESS;
			}
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutput oo = new ObjectOutputStream(bStream);
			oo.writeObject(request);
			byte[] serializedMessage = bStream.toByteArray();
			DatagramSocket datagramSocketfe = new DatagramSocket(FinalVariables.FRONT_PORT_Failure_Port);
			DatagramPacket rtsequencer = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(IP), FinalVariables.RM_LISTENER_PORT_FOR_SOFTWARE_FAILURE);
			datagramSocketfe.send(rtsequencer);
			//  datagramSocketfe.send(rtsequencer);

			System.out.println("sent response to failure notification in RM");
			datagramSocketfe.close();



		} catch(Exception e){
			System.out.println("Fata :"+e);
		}
		System.out.println("System failure/crash notification ");


	}

	private static String getUserServer(String UserID) {
		String UserServer = UserID.substring(0, 3);
		String result = "You can't book";
		switch (UserServer) {
			case "ATW":
				UserServer = "Atwater";
				break;
			case "VER":
				UserServer = "Verdun";
				break;
			case "OUT":
				UserServer = "Outremont";
				break;
		}
		return UserServer;
	}

	;
	
	public String removeMovieSlots (String UserID, String movieID, String movieName) throws IOException, InterruptedException {
		String UserServer = getUserServer(UserID);

		Request request = new Request(UserServer,"removeMovieSlots",0,movieID,movieName,"","","",0);


		int requiredid = sender(request);

		Thread.sleep(4000);

		return CheckSoftwareFailureAndSendResponse(requiredid);

	};
	
	public String listMovieShowsAvailability (String UserID,String movieName) throws IOException, InterruptedException {
		String UserServer = getUserServer(UserID);

		Request request = new Request(UserServer,"listMovieShowsAvailability",0,"",movieName,"","","",0);


		int requiredid = sender(request);

		Thread.sleep(4000);

		return CheckSoftwareFailureAndSendResponse(requiredid);
	};
	
	
	public String seeAll() {
		
		return "";
	};
	//Admin and Cx methods
	
	public String bookMovieTickets (String customerID, String  movieID, String movieName, int numberOfTickets) throws IOException, InterruptedException {

		String UserServer = getUserServer(customerID);

		Request request = new Request(UserServer,"bookMovieTickets",0,movieID,movieName,customerID,"","",numberOfTickets);


		int requiredid = sender(request);

		Thread.sleep(4000);

		return CheckSoftwareFailureAndSendResponse(requiredid);
	};
	
	public String getBookingSchedule (String customerID) throws IOException, InterruptedException {
		String UserServer = getUserServer(customerID);

		Request request = new Request(UserServer,"getBookingSchedule",0,"","",customerID,"","",0);

		int requiredid = sender(request);

		Thread.sleep(4000);

		return CheckSoftwareFailureAndSendResponse(requiredid);
	};
	
	public String cancelMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets) throws IOException, InterruptedException {
		String UserServer = getUserServer(customerID);

		Request request = new Request(UserServer,"cancelMovieTickets",0,movieID,movieName,customerID,"","",numberOfTickets);

		int requiredid = sender(request);

		Thread.sleep(4000);

		return CheckSoftwareFailureAndSendResponse(requiredid);
	};
	
	public String exchangeTickets (String customerID, String movieID, String movieName, String new_movieID, String new_movieName, int numberOfTickets) throws IOException, InterruptedException {
		String UserServer = getUserServer(customerID);

		Request request = new Request(UserServer,"exchangeTickets",0,movieID,movieName,customerID,new_movieID,new_movieName,numberOfTickets);

		int requiredid = sender(request);

		Thread.sleep(4000);

		return CheckSoftwareFailureAndSendResponse(requiredid);
	};
}
