package com.epimorphics.govData.URISets.intervalServer.util;

import java.util.Calendar;
import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;


public class CalendarUtils {
	
	public static int getWeekOfYearYear(EnglishCalendar cal) {
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH)+1 - Calendar.JANUARY;
		int w = cal.get(Calendar.WEEK_OF_YEAR);
		
		if(w==1 && m==12)
			return y+1;
		
		if(w>50 && m==1)
			return y-1;
		
		return y;
	}
	public static boolean inCutOverAnomaly(EnglishCalendar cal) {
        int month = cal.get(Calendar.MONTH)+1-Calendar.JANUARY;
        int year = cal.get(Calendar.YEAR);
        int woy = cal.get(Calendar.WEEK_OF_YEAR);
        
        return (year == 1752 && woy>37);
	}

	public static boolean inCutOverAnomaly(int year, int woy) {
        return (year == 1752 && woy>37);
	}
	
	public static void setWeekOfYear(int year, int woy, EnglishCalendar cal) {
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
	
	public static String makeIsoDuration(int dYears, int dMonths, int dDays,
										 int dHours, int dMins,   int dSecs) {
		
		StringBuilder sb = new StringBuilder();
		if( dYears  >= 0 ||
		    dMonths >= 0 ||
		    dDays   >= 0 ||
		    dHours  >= 0 ||
		    dMins   >= 0 ||
		    dSecs   >= 0 ) {
			sb.append("P");
		} else {
			return null;
		}
		
		if(dYears>=0)
			sb.append(dYears+"Y");
		if(dMonths>=0)
			sb.append(dMonths+"M");
		if(dDays>=0)
			sb.append(dDays+"D");
		
		if( dHours  >= 0 ||
			dMins   >= 0 ||
			dSecs   >= 0 ) {
			sb.append("T");
		} else {
			return sb.toString();
		}

		if(dHours>=0)
			sb.append(dHours+"H");
		if(dMins>=0)
			sb.append(dMins+"M");
		if(dSecs>=0)
			sb.append(dSecs+"S");

		return sb.toString();
	}
	


}
