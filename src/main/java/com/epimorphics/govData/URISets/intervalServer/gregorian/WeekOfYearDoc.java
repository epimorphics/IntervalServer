package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
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


@Path(URITemplate.WEEK_DOC_STEM)
public class WeekOfYearDoc extends Doc {

	protected void reset(int year, int week) {
		reset();
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		try {
			CalendarUtils.setWeekOfYear(year, week, cal);	
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}
		
		this.woy_week = week;
		this.woy_year = year;
		
		this.year = cal.get(Calendar.YEAR);
		this.month = cal.get(Calendar.MONTH) + 1 - Calendar.JANUARY;
		this.day = cal.get(Calendar.DAY_OF_MONTH);
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
	}
	
	@GET
	@Path(WOY_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, week);
		this.ext   = ext;
		return doGet();
	}
	
	@GET
	@Path(WOY_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week) {
		reset(year, week);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(WOY_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week) {
		reset(year, week);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();
	}

	@GET
	@Path(WOY_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week) {
		reset(year, week);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(WOY_PATTERN)
	@Produces({ "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week) {
		reset(year, week);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}
		
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int woy) {
		String relPart = year + WEEK_PREFIX + String.format("%02d", woy);
	
		String s_weekURI = base + WEEK_ID_STEM + relPart;
		Resource r_week = m.createResource(s_weekURI, INTERVALS.Iso8601Week);
		
		String s_label = "Iso8601 Week:" + relPart;
		m.add(r_week, SKOS.prefLabel, s_label, "en");
		m.add(r_week, RDFS.label, s_label, "en");
	
		m.add(r_week, RDFS.comment, "Week " + woy + " of " + year);
			
		return r_week;
	}

	static protected Resource createResource(URI base, Model m, int year, int woy) {
		Resource r_week = createResourceAndLabels(base, m, year, woy);
		m.add(r_week, RDF.type, SCOVO.Dimension);


		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		CalendarUtils.setWeekOfYear(year, woy , cal);

		m.add(r_week, INTERVALS.hasXsdDurationDescription, oneWeek);
		m.add(r_week, TIME.hasDurationDescription, INTERVALS.one_week );
		m.add(r_week, SCOVO.min, formatScvDate(cal, iso8601dateformat), XSDDatatype.XSDdate);

		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_week, TIME.hasBeginning, r_instant);
		cal.add(Calendar.DATE, 7);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_week, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_week, SCOVO.max, formatScvDate(cal, iso8601dateformat), XSDDatatype.XSDdate);

		return r_week;
	}

	@Override
	void addContainedIntervals() {
		ArrayList<Resource> days = new ArrayList<Resource>();
		GregorianOnlyCalendar cal = (GregorianOnlyCalendar) startTime.clone();
		int i_initial_woy = cal.get(Calendar.WEEK_OF_YEAR);
		while(cal.get(Calendar.WEEK_OF_YEAR) == i_initial_woy) {
			Resource r_day = DayDoc.createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
			connectToContainingInterval(model, r_thisTemporalEntity, r_day);
			days.add(r_day);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}		
		RDFList r_days = model.createList(days.iterator());
		model.add(r_thisTemporalEntity,INTERVALS.intervalContainsDays, r_days);
	}

	@Override
	void addContainingIntervals() {
		//TODO
	}

	@Override
	void addNeighboringIntervals() {
		GregorianOnlyCalendar cal;
		Resource r_nextWeek = null;
		Resource r_prevWeek = null;
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.DATE,7);
			r_nextWeek = createResourceAndLabels(base, model, CalendarUtils.getWeekOfYearYear(cal),cal.get(Calendar.WEEK_OF_YEAR));
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.DATE,-7);
			r_prevWeek = createResourceAndLabels(base, model, CalendarUtils.getWeekOfYearYear(cal) ,cal.get(Calendar.WEEK_OF_YEAR));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		// Link adjacent months
		connectToNeigbours(model, r_thisTemporalEntity, r_nextWeek, r_prevWeek);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, woy_week);
		
		addGeneralIntervalTimeLink(model, startTime, oneWeek);		
	}


}