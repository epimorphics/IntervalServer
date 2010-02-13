/******************************************************************
 * File:        MinuteDoc.java
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

package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
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

@Path(URITemplate.MINUTE_DOC_STEM)
public class MinuteDoc extends Doc {
	
	protected void reset(int year, int month, int day, int hour, int min) {
		reset();	
		setWeekOfYearAndMonth(year, month, day);
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		this.hour = hour;
		this.min = min;
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
	}
	
	@GET
	@Path(MINUTE_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
 			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, month, day, hour, min);
		this.ext   = ext;
		return doGet();
	}

	@GET
	@Path(MINUTE_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min ) {
		reset(year, month, day, hour, min);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(MINUTE_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min ) {
		reset(year, month, day, hour, min);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();
	}

	@GET
	@Path(MINUTE_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min ) {
		reset(year, month, day, hour, min);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(MINUTE_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min ) {
		reset(year, month, day, hour, min);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();

	}

	protected static Resource createResourceAndLabels(URI base, Model m, int year,	int moy, int dom, int hod, int moh) {
		String relPart = year + MONTH_PREFIX + String.format("%02d", moy)
				+ DAY_PREFIX + String.format("%02d", dom) + HOUR_PREFIX
				+ String.format("%02d", hod) + MINUTE_PREFIX
				+ String.format("%02d", moh);

		String s_minURI = base + MINUTE_ID_STEM + relPart;
		Resource r_min = m.createResource(s_minURI, INTERVALS.CalendarMinute);

		String s_label = "Gregorian Minute:" + relPart;

		m.add(r_min, SKOS.prefLabel, s_label, "en");
		m.add(r_min, RDFS.label, s_label, "en");

		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.set(year, moy - 1, dom, 0, 0, 0);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod+1);
		String s_mohSuffix = getDecimalSuffix(moh+1);

		// String s_dayOfMonth = cal.getDisplayName(Calendar.DAY_OF_MONTH,
		// Calendar.LONG , Locale.UK);
		m.add(r_min, RDFS.comment, "The " + (moh+1) + s_mohSuffix + " minute of " + (hod+1)
				+ s_hodSuffix + " hour of " + s_dayOfWeek + " the " + dom
				+ s_domSuffix + " " + s_month + " of the Gregorian calendar year " + year, "en");

		return r_min;
	}

	protected static Resource createResource(URI base, Model m, int year, int moy, int dom, int hod, int moh) {
		Resource r_min = createResourceAndLabels(base, m, year, moy, dom, hod, moh);
		m.add(r_min, RDF.type, SCOVO.Dimension);

		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(year, moy-1, dom, hod, moh, 0);
		cal.setLenient(false);

		m.add(r_min, INTERVALS.hasXsdDurationDescription, oneMinute);
		m.add(r_min, TIME.hasDurationDescription, INTERVALS.one_minute);
	
		Resource r_instant = InstantDoc.createResource(base, m, cal);		
		m.add(r_min, TIME.hasBeginning, r_instant);
		m.add(r_min, SCOVO.min, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		
		cal.add(Calendar.MINUTE, 1);
		
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);		
		m.add(r_min, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_min, SCOVO.max, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		
		return r_min;
	}

	@Override
	void addContainedIntervals() {
		GregorianOnlyCalendar cal = (GregorianOnlyCalendar) startTime.clone();
		ArrayList<Resource> seconds = new ArrayList<Resource>();

		// Add the hours to the day
		cal.set(year, month-1, day, hour, 0);
		int i_max_som = cal.getActualMaximum(Calendar.SECOND);
		int i_min_som = cal.getActualMinimum(Calendar.SECOND);
		
		for (int sec = i_min_som; sec <= i_max_som; sec++) {
			Resource r_sec = SecDoc.createResourceAndLabels(base, model, year, month, day, hour, min, sec);
			connectToContainingInterval(model, r_thisTemporalEntity, r_sec);
			seconds.add(r_sec);
		}		
		RDFList r_seconds = model.createList(seconds.iterator());
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsSeconds, r_seconds);
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model, year);
		// Create resources for this quarter and its neighbours.
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf    = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth   = MonthDoc.createResourceAndLabels(base, model, year, month);
		Resource r_thisDay     = DayDoc.createResourceAndLabels(base, model, year, month, day);
		Resource r_thisHour    = HourDoc.createResourceAndLabels(base, model, year, month, day, hour);
		Resource r_thisWeek    = WeekOfYearDoc.createResource(base, model, woy_year, woy_week);
		
		// Link minute to its containing year, half, quarter, day and hour.
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisDay, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHour, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisWeek, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource r_nextMin, r_prevMin;
		GregorianOnlyCalendar cal;
		
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.MINUTE,1);
			r_nextMin = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
			cal.set(year, month-1, day, hour, min);
			cal.add(Calendar.MINUTE,-1);
			r_prevMin = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
			cal.set(year, month-1, day, hour, min);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);

		}
		connectToNeigbours(model, r_thisTemporalEntity, r_nextMin, r_prevMin);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month ,day ,hour, min);
		addGeneralIntervalTimeLink(model, startTime, oneMinute);
	}
}