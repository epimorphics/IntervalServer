/******************************************************************
 * File:        GregorianOnlyCalendar.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
 * 
 * (c) Copyright 2010, Epimorphics Limited
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * $Id:  $
 *****************************************************************/

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
		super();
		setGregorianChangeDate();
		set(year, month, dayOfMonth);
	}

	public GregorianOnlyCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super();
		setGregorianChangeDate();
		set(year, month, dayOfMonth, hourOfDay, minute);
	}

	public GregorianOnlyCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super();
		setGregorianChangeDate();
		set(year, month, dayOfMonth, hourOfDay, minute, second);
	}
	
	private void setGregorianChangeDate() {
//		this.setGregorianChange(changeDate.getTime());		
		this.setGregorianChange(new Date(Long.MIN_VALUE));		
	}

}
