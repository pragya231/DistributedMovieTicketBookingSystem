package request;

import java.io.Serializable;

public class Request implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static  int requestNo=0;
	private int requestId;
	private boolean isSWFail = false;
	private String RM1response;

	public String getRM1response() {
		return RM1response;
	}

	public void setRM1response(String RM1response) {
		this.RM1response = RM1response;
	}

	public String getRM2response() {
		return RM2response;
	}

	public void setRM2response(String RM2response) {
		this.RM2response = RM2response;
	}

	public String getRM3response() {
		return RM3response;
	}

	public void setRM3response(String RM3response) {
		this.RM3response = RM3response;
	}

	private String RM2response;
	private String RM3response;

	private String customerID;
	private String movieID;

	private String movieName;
	private String new_movieID;
	private String new_movieName;
	private int numberOfTickets;
	private String functionName;
	private String UserServer;


	private int capacity;

	public boolean isSWFail() {
		return isSWFail;
	}

	public void setSWFail(boolean SWFail) {
		isSWFail = SWFail;
	}



	public static int getRequestNo() {
		return requestNo;
	}

	public static void setRequestNo(int requestNo) {
		Request.requestNo = requestNo;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getMovieID() {
		return movieID;
	}

	public void setMovieID(String movieID) {
		this.movieID = movieID;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getNew_movieID() {
		return new_movieID;
	}

	public void setNew_movieID(String new_movieID) {
		this.new_movieID = new_movieID;
	}

	public String getNew_movieName() {
		return new_movieName;
	}

	public void setNew_movieName(String new_movieName) {
		this.new_movieName = new_movieName;
	}

	public int getNumberOfTickets() {
		return numberOfTickets;
	}

	public void setNumberOfTickets(int numberOfTickets) {
		this.numberOfTickets = numberOfTickets;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getUserServer() {
		return UserServer;
	}

	public void setUserServer(String userServer) {
		UserServer = userServer;
	}
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	private String RMId="";
	public String getRMId() {
		return RMId;
	}

	public void setRMId(String rMId) {
		RMId = rMId;
	}
	public Request(){
		this.requestId = requestNo;
		this.RM1response ="";
		this.RM2response ="";
		this.RM3response ="";
		this.requestNo+=1;


	}


	public Request(String UserServer,String functionName,int capacity,String movieID,
				   String movieName, String customerID, String new_movieID, String new_movieName, int numberOfTickets){

		this.UserServer=UserServer;
		this.functionName=functionName;
		this.capacity=capacity;
		this.movieID=movieID;
		this.movieName=movieName;
		this.customerID=customerID;
		this.new_movieID=new_movieID;
		this.new_movieName=new_movieName;
		this.numberOfTickets=numberOfTickets;
	}

	@Override
	public String toString() {
		return "Request{" +
				"requestId=" + requestId +
				", isSWFail=" + isSWFail +
				", RM1response='" + RM1response + '\'' +
				", RM2response='" + RM2response + '\'' +
				", RM3response='" + RM3response + '\'' +
				", customerID='" + customerID + '\'' +
				", movieID='" + movieID + '\'' +
				", movieName='" + movieName + '\'' +
				", new_movieID='" + new_movieID + '\'' +
				", new_movieName='" + new_movieName + '\'' +
				", numberOfTickets=" + numberOfTickets +
				", functionName='" + functionName + '\'' +
				", UserServer='" + UserServer + '\'' +
				", capacity=" + capacity +
				", RMId='" + RMId + '\'' +
				'}';
	}

	//	public String toString() {
//		return this.requestId + "  -  "+this.query+"  -  "+this.response;
//	}
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	
	
}