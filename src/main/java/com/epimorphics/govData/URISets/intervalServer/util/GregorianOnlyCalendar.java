package com.epimorphics.govData.URISets.intervalServer.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class GregorianOnlyCalendar extends GregorianCalendar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6297956560693049540L;
	
//	private static GregorianCalendar changeDate = new GregorianCalendar(1752, Calendar.SEPTEMBER, 14, 0, 0, 0);

	public GregorianOnlyCalendar() {
		super();
		setGregorianChangeDate();
	}
	
	public GregorianOnlyCalendar(Locale locale) {
		super(locale);
		setGregorianChangeDate();
	}

	public GregorianOnlyCalendar(TimeZone zone) {
		super(zone);
		setGregorianChangeDate();
	}

	public GregorianOnlyCalendar(TimeZone zone, Locale locale) {
		super(zone, locale);
		setGregorianChangeDate();
	}

	public GregorianOnlyCalendar(int year, int month, int dayOfMonth) {
		super(year, month, dayOfMonth);
		setGregorianChangeDate();
	}

	public GregorianOnlyCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super(year, month, dayOfMonth, hourOfDay, minute);
		setGregorianChangeDate();
	}

	public GregorianOnlyCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super(year, month, dayOfMonth, hourOfDay, minute, second);
		setGregorianChangeDate();
	}
	
	private void setGregorianChangeDate() {
//		this.setGregorianChange(changeDate.getTime());		
		this.setGregorianChange(new Date(Long.MIN_VALUE));		
	}

}
