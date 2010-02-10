package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

@Path(URITemplate.DAY_DOC_STEM)
public class DayDoc extends Doc {
	
	protected void reset(int year, int month, int day) {
		reset();
		setWeekOfYearAndMonth(year, month, day);		
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
	}
	
	@GET
	@Path(DAY_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, month, day);
		this.ext   = ext;
		return doGet();
	}

	@GET
	@Path(DAY_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN) int day ) {
		reset(year, month, day);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(DAY_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN) int day ) {
		reset(year, month, day);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();
	}

	@GET
	@Path(DAY_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN) int day ) {
		reset(year, month, day);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(DAY_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN) int day ) {
		reset(year, month, day);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}
	
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int moy, int dom) {
		String relPart = year + MONTH_PREFIX + String.format("%02d", moy)
				+ DAY_PREFIX + String.format("%02d", dom);
	
		String s_dayURI = base + DAY_ID_STEM + relPart;
		Resource r_day = m.createResource(s_dayURI, INTERVALS.CalendarDay);

		
		String s_label = "Calendar Day:" + relPart;
		m.add(r_day, SKOS.prefLabel, s_label, "en");
		m.add(r_day, RDFS.label, s_label, "en");
	
		GregorianCalendar cal = new GregorianCalendar(Locale.UK);
		cal.set(year, moy - 1, dom);
	
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		m.add(r_day, RDFS.comment, s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month + " " + year, "en");
	
		return r_day;
	}

	static protected Resource createResource(URI base, Model m, int year, int moy, int dom) {
		Resource r_day = createResourceAndLabels(base, m, year, moy, dom);
		m.add(r_day, RDF.type, SCOVO.Dimension);
		GregorianCalendar cal = new GregorianCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, moy-1, dom,0 , 0, 0);
		cal.getTimeInMillis();
		
		
		m.add(r_day, INTERVALS.hasXsdDurationDescription, oneDay);
		m.add(r_day, TIME.hasDurationDescription, INTERVALS.one_day);
		m.add(r_day, SCOVO.min, formatScvDate(cal, iso8601dateTimeformat), XSDDatatype.XSDdateTime);


		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_day, TIME.hasBeginning, r_instant);

		int i_dow = cal.get(Calendar.DAY_OF_WEEK);
		setDayOfWeek(m, r_day, i_dow);

		cal.add(Calendar.DAY_OF_MONTH,1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_day, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_day, SCOVO.max, formatScvDate(cal, iso8601dateTimeformat), XSDDatatype.XSDdateTime);


		return r_day;
	}

	@Override
	void addContainedIntervals() {
		GregorianCalendar cal = (GregorianCalendar) startTime.clone();
		ArrayList<Resource> hours = new ArrayList<Resource>();
		
		cal.set(year, month-1, day);
		int i_max_hid = cal.getActualMaximum(Calendar.HOUR_OF_DAY);
		int i_min_hid = cal.getActualMinimum(Calendar.HOUR_OF_DAY);
		
		for (int hour = i_min_hid; hour <= i_max_hid; hour++) {
			Resource r_hour = HourDoc.createResourceAndLabels(base, model, year, month, day, hour);
			connectToContainingInterval(model, r_thisTemporalEntity, r_hour);
			hours.add(r_hour);
		}
		RDFList r_hours = model.createList(hours.iterator());
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsHours, r_hours);
		
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model,year);
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth = MonthDoc.createResourceAndLabels(base, model, year, month);
		
		Resource r_thisWeek = WeekOfYearDoc.createResource(base, model, woy_year, woy_week); /**/
		
		// Link day to its containing year, half, quarter and month
		connectToContainingInterval(model, r_thisYear,  r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf,  r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);

		connectToContainingInterval(model, r_thisWeek,  r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource r_nextDay, r_prevDay;
		GregorianCalendar cal;
		
		try {
			cal = (GregorianCalendar) startTime.clone();
			cal.add(Calendar.DAY_OF_MONTH,1);
			r_nextDay = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
			cal = (GregorianCalendar) startTime.clone();
			cal.add(Calendar.DAY_OF_MONTH,-1);
			r_prevDay = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}		
		// Link adjacent days
		connectToNeigbours(model, r_thisTemporalEntity, r_nextDay, r_prevDay);
	}

	@Override
	void addThisInterval() {
		r_thisTemporalEntity = createResource(base, model, year, month, day);
		
		/*
		int woy_week = startTime.get(Calendar.WEEK_OF_YEAR);
		int woy_dow = startTime.get(Calendar.DAY_OF_WEEK)+1-Calendar.MONDAY;
		int woy_year = year+
					   (((month==1 )&&(woy_week>51)) ? -1 :
						((month==12)&&(woy_week==1)) ? -1 : 0);

		Resource r_woyDay = model.createResource(base+"calendar-day-weekdate/"+woy_year+"-W"+woy_week+"D"+woy_dow);
		model.add(r_thisTemporalEntity, TIME.intervalEquals, r_woyDay);
        */
		addPlaceTimeLink(model, startTime.getTime(), oneDay);	
	}
	
}