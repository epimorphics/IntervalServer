package com.epimorphics.govData.URISets.intervalServer.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class EnglishCalendar extends GregorianCalendar {

	public EnglishCalendar() {
		super();
		setGregorianChangeDate();
	}
	
	public EnglishCalendar(Locale locale) {
		super(locale);
		setGregorianChangeDate();
	}

	public EnglishCalendar(TimeZone zone) {
		super(zone);
		setGregorianChangeDate();
	}

	public EnglishCalendar(TimeZone zone, Locale locale) {
		super(zone, locale);
		setGregorianChangeDate();
	}

	public EnglishCalendar(int year, int month, int dayOfMonth) {
		super(year, month, dayOfMonth);
		setGregorianChangeDate();
	}

	public EnglishCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super(year, month, dayOfMonth, hourOfDay, minute);
		setGregorianChangeDate();
	}

	public EnglishCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super(year, month, dayOfMonth, hourOfDay, minute, second);
		setGregorianChangeDate();
	}
	
	private void setGregorianChangeDate() {
		GregorianCalendar c = new GregorianCalendar(1752, Calendar.SEPTEMBER, 14, 0, 0, 0);
		this.setGregorianChange(c.getTime());		
	}

}
