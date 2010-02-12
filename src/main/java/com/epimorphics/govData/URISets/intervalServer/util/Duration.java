package com.epimorphics.govData.URISets.intervalServer.util;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Duration {

	protected static int DURATION_YEARS = 2;
	protected static int DURATION_MONTHS = 4;
	protected static int DURATION_DAYS = 6;
	protected static int DURATION_HOURS = 9;
	protected static int DURATION_MINUTES = 11;
	protected static int DURATION_SECONDS = 13;
	
	protected static final String DURATION_REGEX = "P(([0-9]+)Y)?(([0-9]+)M)?(([0-9]+)D)?(T(([0-9]+)H)?(([0-9]+)M)?(([0-9]+)S)?)?";
	//                                                ^2          ^4          ^6         ^7 ^9          ^11         ^13

	/**
	 * @return the years
	 */
	public int getYears() {
		return years;
	}

	/**
	 * @return the months
	 */
	public int getMonths() {
		return months;
	}

	/**
	 * @return the days
	 */
	public int getDays() {
		return days;
	}

	/**
	 * @return the hours
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * @return the mins
	 */
	public int getMins() {
		return mins;
	}

	/**
	 * @return the secs
	 */
	public int getSecs() {
		return secs;
	}

	private int years = 0;
	private int months = 0;
	private int days = 0;
	private int hours = 0;
	private int mins = 0;
	private int secs = 0;

	public Duration(String duration) {
		Pattern p = Pattern.compile(DURATION_REGEX);
		Matcher m = p.matcher(duration);

		if (m.matches()) {
			String s = m.group(DURATION_YEARS);
			if (s != null && !s.equals(""))
				years = Integer.parseInt(s);

			s = m.group(DURATION_MONTHS);
			if (s != null && !s.equals(""))
				months = Integer.parseInt(s);

			s = m.group(DURATION_DAYS);
			if (s != null && !s.equals(""))
				days = Integer.parseInt(s);

			s = m.group(DURATION_HOURS);
			if (s != null && !s.equals(""))
				hours = Integer.parseInt(s);

			s = m.group(DURATION_MINUTES);
			if (s != null && !s.equals(""))
				mins = Integer.parseInt(s);

			s = m.group(DURATION_SECONDS);
			if (s != null && !s.equals(""))
				secs = Integer.parseInt(s);
		}
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		if (years > 0 || months > 0 || days > 0 || hours > 0 || mins > 0
				|| secs > 0) {
			sb.append("P");
		} else {
			return null;
		}

		if (years > 0)
			sb.append(years + "Y");

		if (months > 0)
			sb.append(months + "M");

		if (days > 0)
			sb.append(days + "D");

		if (hours > 0 || mins > 0 || secs > 0) {
			sb.append("T");
		} else {
			return sb.toString();
		}

		if (hours > 0)
			sb.append(hours + "H");
		if (mins > 0)
			sb.append(mins + "M");
		if (secs > 0)
			sb.append(secs + "S");

		return sb.toString();
	}

	public void addToCalendar(Calendar cal) {
		if(years>0)
			cal.add(Calendar.YEAR, years);

		if(months>0)
			cal.add(Calendar.MONTH, months);

		if(days>0)
			cal.add(Calendar.DATE, days);
		
		if(hours>0)
			cal.add(Calendar.HOUR, hours);
		
		if(mins>0)
			cal.add(Calendar.MINUTE, mins);
		
		if(secs>0)
			cal.add(Calendar.SECOND, secs);
	}

}
