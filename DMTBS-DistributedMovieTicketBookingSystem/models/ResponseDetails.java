package models;

import static FinalVariables.FinalVariables.*;

public class ResponseDetails {
	String responseFromRM1="";
	String responseFromRM2="";
	String responseFromRM3="";
	int sequenceNumber;
	
	public ResponseDetails()
	{
		responseFromRM1="NULL";
		responseFromRM2="NULL";
		responseFromRM3="NULL";
		sequenceNumber=0;
	}
	public void setResponse(String responseFromRM,String ipAddress)
	{
		switch (ipAddress.substring(1)) {
		  case RM1_IP_ADDRESS:
			  this.setResponseFromRM1(responseFromRM);
		      break;
		  case RM2_IP_ADDRESS:
			  this.setResponseFromRM2(responseFromRM);
		      break;
		  case RM3_IP_ADDRESS:
			  this.setResponseFromRM3(responseFromRM);
		      break;

		}
	}
	public String getResponseFromRM1() {
		return responseFromRM1;
	}
	public void setResponseFromRM1(String responseFromRM1) {
		this.responseFromRM1 = responseFromRM1;
	}
	public String getResponseFromRM2() {
		return responseFromRM2;
	}
	public void setResponseFromRM2(String responseFromRM2) {
		this.responseFromRM2 = responseFromRM2;
	}
	public String getResponseFromRM3() {
		return responseFromRM3;
	}
	public void setResponseFromRM3(String responseFromRM3) {
		this.responseFromRM3 = responseFromRM3;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}
