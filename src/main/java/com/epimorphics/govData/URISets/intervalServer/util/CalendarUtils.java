package com.epimorphics.govData.URISets.intervalServer.util;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class CalendarUtils {
	
	public static int getWeekOfYearYear(GregorianCalendar cal) {
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH)+1 - Calendar.JANUARY;
		int w = cal.get(Calendar.WEEK_OF_YEAR);
		
		if(w==1 && m==12)
			return y+1;
		
		if(w>50 && m==1)
			return y-1;
		
		return y;
	}
	public static boolean inCutOverAnomaly(GregorianCalendar cal) {
        int month = cal.get(Calendar.MONTH)+1-Calendar.JANUARY;
        int year = cal.get(Calendar.YEAR);
        int woy = cal.get(Calendar.WEEK_OF_YEAR);
        
        return (year == 1582 && woy>41);
	}

	public static boolean inCutOverAnomaly(int year, int woy) {
        return (year == 1582 && woy>41);
	}
	
	public static void setWeekOfYear(int year, int woy, GregorianCalendar cal) {
		boolean l = cal.isLenient();
		cal.setLenient(true);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, woy);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTimeInMillis();
        int cal_woy_week = cal.get(Calendar.WEEK_OF_YEAR);
 		if( woy!=cal_woy_week &&
			!inCutOverAnomaly(cal) &&
			woy-cal_woy_week !=1 ) {	
			throw new IllegalArgumentException("Invalid Week of Year: "+year+"-W"+woy);
		}
		cal.setLenient(l);
	}
	


}
