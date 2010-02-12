package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;
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


@Path(URITemplate.MONTH_DOC_STEM)
public class MonthDoc extends Doc {

	protected void reset(int year, int month) {
		reset();
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
	}
	
	@GET
	@Path(MONTH_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, month);
		this.ext   = ext;
		return doGet();
	}
	
	@GET
	@Path(MONTH_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month) {
		reset(year, month);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(MONTH_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month) {
		reset(year, month);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();
	}

	@GET
	@Path(MONTH_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month) {
		reset(year, month);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(MONTH_PATTERN)
	@Produces({ "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month) {
		reset(year, month);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}
		
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int moy) {
		String relPart = year + MONTH_PREFIX + String.format("%02d", moy);
	
		String s_monthURI = base + MONTH_ID_STEM + relPart;
		Resource r_month = m.createResource(s_monthURI, INTERVALS.CalendarMonth);
		
		String s_label = "Calendar Month:" + relPart;
		m.add(r_month, SKOS.prefLabel, s_label, "en");
		m.add(r_month, RDFS.label, s_label, "en");
	
		EnglishCalendar cal = new EnglishCalendar(Locale.UK);
		cal.set(year, moy - 1, 01);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.UK);
	
		m.add(r_month, RDFS.comment, "The month of " + s_month
				+ " in calendar year " + year, "en");
			
		return r_month;
	}

	static protected Resource createResource(URI base, Model m, int year, int moy) {
		Resource r_month = createResourceAndLabels(base, m, year, moy);
		m.add(r_month, RDF.type, SCOVO.Dimension);

		EnglishCalendar cal = new EnglishCalendar(year, moy-1, 1, 0, 0, 0);
		cal.setLenient(false);
				
		m.add(r_month, INTERVALS.hasXsdDurationDescription, oneMonth);
		m.add(r_month, TIME.hasDurationDescription, INTERVALS.one_month );
		m.add(r_month, SCOVO.min, formatScvDate(cal, iso8601dateformat), XSDDatatype.XSDdate);


		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_month, TIME.hasBeginning, r_instant);
		cal.add(Calendar.MONTH, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_month, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_month, SCOVO.max, formatScvDate(cal, iso8601dateformat), XSDDatatype.XSDdate);

		return r_month;
	}

	@Override
	void addContainedIntervals() {
		ArrayList<Resource> days = new ArrayList<Resource>();
		EnglishCalendar cal = (EnglishCalendar) startTime.clone();
		while(cal.get(Calendar.MONTH)==(month-1)) {
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
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model,year);
		Resource r_thisHalf = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);

		// Link month to its containing year, half and quarter
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		EnglishCalendar cal;
		Resource r_nextMonth = null;
		Resource r_prevMonth = null;
		try {
			cal = (EnglishCalendar) startTime.clone();
			cal.add(Calendar.MONTH,1);
			r_nextMonth = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1);
			cal = (EnglishCalendar) startTime.clone();
			cal.add(Calendar.MONTH,-1);
			r_prevMonth = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		// Link adjacent months
		connectToNeigbours(model, r_thisTemporalEntity, r_nextMonth, r_prevMonth);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month);
		
		addGeneralIntervalTimeLink(model, startTime.getTime(), oneMonth);		
	}

}