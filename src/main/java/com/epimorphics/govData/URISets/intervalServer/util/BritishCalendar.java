/******************************************************************
 * File:        BritishCalendar.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
 * 
 * (c) Copyright 2010, Epimorphics Limited
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 * $Id:  $
 *****************************************************************/

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
		//Zero the milliseconds as well.
		set(Calendar.MILLISECOND, 0);

	}
	
	public BritishCalendar(Locale locale) {
		super(locale);
		setGregorianChangeDate();
		//Zero the milliseconds as well.
		set(Calendar.MILLISECOND, 0);

	}

	public BritishCalendar(TimeZone zone) {
		super(zone);
		setGregorianChangeDate();
		//Zero the milliseconds as well.
		set(Calendar.MILLISECOND, 0);

	}

	public BritishCalendar(TimeZone zone, Locale locale) {
		super(zone, locale);
		setGregorianChangeDate();
		//Zero the milliseconds as well.
		set(Calendar.MILLISECOND, 0);
	}

	public BritishCalendar(int year, int month, int dayOfMonth) {
		super();
		setGregorianChangeDate();
		set(year, month, dayOfMonth);
		//Zero the milliseconds as well.
		set(Calendar.MILLISECOND, 0);

	}

	public BritishCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super();
		setGregorianChangeDate();
		set(year, month, dayOfMonth, hourOfDay, minute);
		//Zero the milliseconds as well.
		set(Calendar.MILLISECOND, 0);
	}

	public BritishCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super();
		setGregorianChangeDate();
		set(year, month, dayOfMonth, hourOfDay, minute, second);
		//Zero the milliseconds as well.
		set(Calendar.MILLISECOND, 0);
	}
	
	private void setGregorianChangeDate() {
		this.setGregorianChange(changeDate.getTime());		
	}
	
	public Object clone() {
		BritishCalendar res  = (BritishCalendar) super.clone();
		setGregorianChangeDate();
			
		return res;
	}
}
