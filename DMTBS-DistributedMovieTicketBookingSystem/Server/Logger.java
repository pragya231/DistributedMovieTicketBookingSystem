package Server;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	static boolean isServer;
	
	 public static String getLogFileName(String name,boolean isServer) {
	        String rootDir ="D:\\Shivam\\SEM4\\comp6231\\Assignment2";
	        		//System.getProperty("user.dir");
	        String logName = null;
	        if (isServer) {
	            if (name.equals("ATW"))
	                logName = rootDir + "\\Logs\\Server_Logs\\Atwater.txt";
	            else if (name.equals("VER"))
	                logName = rootDir + "\\Logs\\Server_Logs\\Verdun.txt";
	            else if (name.equals("OUT")) 
	                logName = rootDir + "\\Logs\\Server_Logs\\Outremont.txt";
	        } 
	        else 
	            logName = rootDir + "\\Logs\\Client_Logs\\" + name + ".txt";
	        //System.out.println(logName);
	        return logName;
	    }
	
	public static void enterLog(String id, String msg) throws IOException {
		if(id.equals("ATW")||id.equals("VER")||id.equals("OUT"))
			isServer=true;
		else
			isServer=false;
		//System.out.println(isServer);
		FileWriter fileWriter = new FileWriter(getLogFileName(id,isServer), true);
       PrintWriter printWriter = new PrintWriter(fileWriter);
       printWriter.println( getFDate() + " " + msg);
       printWriter.close();
	}
	
	public static String getFDate() {
    	Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        return dateFormat.format(date);
    }
}

