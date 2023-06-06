package Server;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.Client;

public class CheckData {
	public static boolean checkDateLimit(String s) {
		int dt = Integer.parseInt(s.substring(0,2));
		int mth = Integer.parseInt(s.substring(2,4));
		int yr = Integer.parseInt(s.substring(4,6)) 
		+ 2000;
		System.out.println(dt+ " "+mth + " "+yr);
		if(yr==Client.currentdate.getYear())
			if(mth == Client.currentdate.getMonthValue())
				if(dt>=Client.currentdate.getDayOfMonth() &&
				dt<= Client.currentdate.getDayOfMonth() +7 )
					return true;
		return false;
	}
	
	public static boolean checkDateFormat(String s) {
		if(s.equals("00"))
			return true;
		int dt = Integer.parseInt(s.substring(0,2));
		int mth = Integer.parseInt(s.substring(2,4));
		int yr = Integer.parseInt(s.substring(4,6));
		
		if(s.length()==6 && dt>=1 && dt<=31 && mth>=1 && mth <=12 && yr >=23) {
			return true;
		}
		else
			return false;
	}
	

	public static boolean checkUserName(String s) {
		if(s.length()==8 
				&& (s.substring(0, 3).toUpperCase().equals("ATW")||s.substring(0, 3).toUpperCase().equals("VER")||s.substring(0, 3).toUpperCase().equals("OUT"))
				&& (s.substring(3, 4).toUpperCase().equals("A") || s.substring(3, 4).toUpperCase().equals("C")))
			return true;
		else
		return false;
	}
	
		
}

