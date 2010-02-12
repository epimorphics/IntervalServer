package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.util.Calendar;
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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(URITemplate.SECOND_DOC_STEM)
public class SecDoc extends Doc {
	
	protected void reset(int year, int month, int day, int hour, int min, int sec) {
		reset();
		setWeekOfYearAndMonth(year, month, day);
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		this.hour = hour;
		this.min = min;
		this.sec = sec;
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
	}
	
	@GET
	@Path(SECOND_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
 			@PathParam(MINUTE_TOKEN) int min,
 			@PathParam(SECOND_TOKEN) int sec,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, month, day, hour, min, sec);
		this.ext   = ext;
		return doGet();
	}

	@GET
	@Path(SECOND_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(SECOND_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();

	}
	@GET
	@Path(SECOND_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(SECOND_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}

	static protected Resource createResourceAndLabels(URI base, Model m,int year, int moy, int dom, int hod, int moh, int som) {
		String relPart = toXsdDateTime(year, moy, dom, hod, moh, som);
	
		String s_secURI = base + SECOND_ID_STEM + relPart;
		Resource r_sec = m.createResource(s_secURI, INTERVALS.CalendarSecond);
		
		String s_label = "Gregorian Second:" + relPart;
		m.add(r_sec, SKOS.prefLabel, s_label, "en");
		m.add(r_sec, RDFS.label, s_label, "en");
	
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.set(year, moy - 1, dom);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod);
		String s_mohSuffix = getDecimalSuffix(moh);
		String s_somSuffix = getDecimalSuffix(som);
	
		// String s_dayOfMonth = cal.getDisplayName(Calendar.DAY_OF_MONTH,
		// Calendar.LONG , Locale.UK);
		m.add(r_sec, RDFS.comment, (som+1) + s_somSuffix + " second of " + (moh+1)
				+ s_mohSuffix + " minute of " + (hod+1) + s_hodSuffix + " hour of "
				+ s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month
				+ " " + year, "en");
	
		return r_sec;
	}

	static protected Resource createResource(URI base, Model m,	int year, int moy, int dom, int hod, int moh, int som) {
		Resource r_sec = createResourceAndLabels(base, m, year, moy, dom, hod, moh, som);
		m.add(r_sec, RDF.type, SCOVO.Dimension);
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(year, moy-1 , dom, hod, moh, som);
		cal.setLenient(false);

		m.add(r_sec, INTERVALS.hasXsdDurationDescription, oneSecond);
		m.add(r_sec, TIME.hasDurationDescription, INTERVALS.one_second);
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_sec, TIME.hasBeginning, r_instant);
		m.add(r_sec, SCOVO.min, formatScvDate(cal, iso8601dateTimeformat, XSDDatatype.XSDdateTime) );
		
		cal.add(Calendar.SECOND, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_sec, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_sec, SCOVO.max, formatScvDate(cal, iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		return r_sec;
	}

	@Override
	void addContainedIntervals() {
		// Bottomed Out at seconds - nothing to do.
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model,year);
		// Create resources for this quarter and its neighbours.
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf    = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth   = MonthDoc.createResourceAndLabels(base, model, year, month);
		Resource r_thisDay     = DayDoc.createResourceAndLabels(base, model, year, month, day);
		Resource r_thisHour    = HourDoc.createResourceAndLabels(base, model, year, month, day, hour);
		Resource r_thisMin     = MinuteDoc.createResourceAndLabels(base, model, year, month, day, hour, min);
		Resource r_thisWeek    = WeekOfYearDoc.createResource(base, model, woy_year, woy_week);

		// Link second to its containing year, half, quarter, month, day, hour and minute.
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisDay, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHour, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMin, r_thisTemporalEntity);	
		connectToContainingInterval(model, r_thisWeek, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource  r_nextSec, r_prevSec;
		GregorianOnlyCalendar cal;
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.SECOND,1);
			r_nextSec = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
			cal.set(year, month-1, day, hour, min, sec);
			cal.add(Calendar.SECOND,-1);
			r_prevSec = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
			cal.set(year, month-1, day, hour, min, sec);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}
		// Link adjacent seconds
		connectToNeigbours(model, r_thisTemporalEntity, r_nextSec, r_prevSec);	
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month ,day, hour, min, sec);
		addGeneralIntervalTimeLink(model, startTime, oneSecond);
	}
}