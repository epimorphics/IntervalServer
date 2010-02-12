package com.epimorphics.govData.URISets.intervalServer.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class BritishCalendar extends GregorianCalendar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2900153523250912600L;

	private static GregorianCalendar changeDate = new GregorianCalendar(1752, Calendar.SEPTEMBER, 14, 0, 0, 0);

	public BritishCalendar() {
		super();
		setGregorianChangeDate();
	}
	
	public BritishCalendar(Locale locale) {
		super(locale);
		setGregorianChangeDate();
	}

	public BritishCalendar(TimeZone zone) {
		super(zone);
		setGregorianChangeDate();
	}

	public BritishCalendar(TimeZone zone, Locale locale) {
		super(zone, locale);
		setGregorianChangeDate();
	}

	public BritishCalendar(int year, int month, int dayOfMonth) {
		super(year, month, dayOfMonth);
		setGregorianChangeDate();
	}

	public BritishCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super(year, month, dayOfMonth, hourOfDay, minute);
		setGregorianChangeDate();
	}

	public BritishCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super(year, month, dayOfMonth, hourOfDay, minute, second);
		setGregorianChangeDate();
	}
	
	private void setGregorianChangeDate() {
		this.setGregorianChange(changeDate.getTime());		
	}

}
