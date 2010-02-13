/******************************************************************
 * File:        HourDoc.java
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
package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import com.epimorphics.govData.URISets.intervalServer.gregorian.InstantDoc;
import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(URITemplate.HOUR_DOC_STEM)
public class HourDoc extends Doc {
	
	protected void reset(int year, int month, int day, int hour) {
		reset();
		
		setWeekOfYearAndMonth(year, month, day);
		
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		this.hour = hour;
		
		startTime = new BritishCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		startTime.getTimeInMillis();
	}
	
	@GET
	@Path(HOUR_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, month, day, hour);
		this.ext   = ext;
		return doGet();
	}
	
	@GET
	@Path(HOUR_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)  int hour ) {
		reset(year, month, day, hour);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(HOUR_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)  int hour ) {
		reset(year, month, day, hour);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();
	}

	@GET
	@Path(HOUR_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)  int hour ) {
		reset(year, month, day, hour);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(HOUR_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)  int hour ) {
		reset(year, month, day, hour);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}

	protected static Resource createResourceAndLabels(URI base, Model m, int year, int moy, int dom, int hod) {
		String relPart = year + MONTH_PREFIX + String.format("%02d", moy)
				+ DAY_PREFIX + String.format("%02d", dom) + HOUR_PREFIX
				+ String.format("%02d", hod);
	
		String s_hourURI = base + HOUR_ID_STEM + relPart;
		Resource r_hour = m.createResource(s_hourURI, INTERVALS.CalendarHour);

		String s_label = "Calendar Hour:" + relPart;
	
		m.add(r_hour, SKOS.prefLabel, s_label, "en");
		m.add(r_hour, RDFS.label, s_label, "en");
	
		BritishCalendar cal = new BritishCalendar(Locale.UK);
		cal.set(year, moy - 1, dom);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod+1);
	
		// String s_dayOfMonth = cal.getDisplayName(Calendar.DAY_OF_MONTH,
		// Calendar.LONG , Locale.UK);
		m.add(r_hour, RDFS.comment, "The "+ (hod+1) + s_hodSuffix + " hour of "
				+ s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month
				+ " of the British calendar year " + year, "en");
	
		return r_hour;
	}

	protected static Resource createResource(URI base, Model m, int year, int moy, int dom, int hod) {
		Resource r_hour = createResourceAndLabels(base, m, year, moy, dom, hod);
		m.add(r_hour, RDF.type, SCOVO.Dimension);
		BritishCalendar cal = new BritishCalendar(year, moy-1, dom, hod, 0, 0);
		cal.setLenient(false);

		m.add(r_hour, INTERVALS.hasXsdDurationDescription, oneHour);
		m.add(r_hour, TIME.hasDurationDescription, INTERVALS.one_hour);
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_hour, TIME.hasBeginning, r_instant);
		m.add(r_hour, SCOVO.min, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		cal.add(Calendar.HOUR, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_hour, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_hour, SCOVO.max, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		return r_hour;
	}

	@Override
	void addContainedIntervals() {
		BritishCalendar cal = (BritishCalendar) startTime.clone();
		ArrayList<Resource> minutes = new ArrayList<Resource>();
		
		// Add the minutes to the hour of day
		cal.set(year, month-1, day, hour, 0);
		int i_max_moh = cal.getActualMaximum(Calendar.MINUTE);
		int i_min_moh = cal.getActualMinimum(Calendar.MINUTE);
		
		for (int min = i_min_moh; min <= i_max_moh; min++) {
			Resource r_min = MinuteDoc.createResourceAndLabels(base, model, year, month, day, hour, min);
			connectToContainingInterval(model, r_thisTemporalEntity, r_min);
			minutes.add(r_min);
		}
		RDFList r_minutes = model.createList(minutes.iterator());
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsMinutes, r_minutes);
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model,year);
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf    = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth   = MonthDoc.createResourceAndLabels(base, model, year, month);
		Resource r_thisDay     = DayDoc.createResourceAndLabels(base, model, year, month, day);
		
		Resource r_thisWeek = WeekOfYearDoc.createResource(base, model, woy_year, woy_week); 


		// Link hour to its containing year, half, quarter, month and day
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisDay, r_thisTemporalEntity);	
		connectToContainingInterval(model, r_thisWeek,  r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource r_nextHour, r_prevHour;
		BritishCalendar cal;
		
		try {
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.HOUR_OF_DAY,1);
			r_nextHour = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY));
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.DAY_OF_MONTH,-1);
			r_prevHour = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}		
		// Link adjacent months
		connectToNeigbours(model, r_thisTemporalEntity, r_nextHour, r_prevHour);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month, day, hour);
		addGeneralIntervalTimeLink(model, startTime, oneHour);
	}
}