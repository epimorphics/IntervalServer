package com.epimorphics.govData;


import java.util.Calendar;
import java.util.Locale;

import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;



public class Caltest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EnglishCalendar cal = new EnglishCalendar(Locale.UK);
		cal.setLenient(false);

		cal.set(1752, Calendar.SEPTEMBER, 14,0,0,0);
		cal.getTimeInMillis();
		
		System.out.println("GC Change over "+cal.getGregorianChange());
		
		cal.set(1752, Calendar.AUGUST, 31,0,0,0);
		
		System.out.println(cal.getTime());
		for(int i=0; i<32; i++) {
			cal.add(Calendar.DATE, +1);
			System.out.println(cal.getTime());
			System.out.printf("%s %02d %02d %04d\n", 
					dowToString(cal.get(Calendar.DAY_OF_WEEK)),
					cal.get(Calendar.DATE),
					cal.get(Calendar.MONTH)+1-Calendar.JANUARY,
					cal.get(Calendar.YEAR));
					
		}
	}

	public static String dowToString(int i) {
		switch(i) {
		case Calendar.SUNDAY:
			return "Sun";
		case Calendar.MONDAY:
			return "Mon";
		case Calendar.TUESDAY:
			return "Tue";
		case Calendar.WEDNESDAY:
			return "Wed";
		case Calendar.THURSDAY:
			return "Thu";
		case Calendar.FRIDAY:
			return "Fri";
		case Calendar.SATURDAY:
			return "Sat";
			
		}
		return null;
	}

}
