package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.net.URI;
import java.util.Calendar;
import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

@Path(URITemplate.YEAR_DOC_STEM)
public class YearDoc extends Doc {
	
	protected void reset(int year) {
		reset();
		this.year = year;
	}
	
	@GET
	@Path(YEAR_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year);
		this.ext = ext;
		return doGet() ;
	}
	
	@GET
	@Path(YEAR_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN) int year) {
		reset(year);
		return doGetNTriple()
			   .contentLocation(URI.create(ui.getPath()+"."+EXT_NT))
		       .build();
	}

	@GET
	@Path(YEAR_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN) int year) {
		reset(year);
		return doGetRDF()
		       .contentLocation(URI.create(ui.getPath()+"."+EXT_RDF))
		       .build();
	}

	@GET
	@Path(YEAR_PATTERN)
	@Produces( "application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN) int year) {
		reset(year);
		return doGetJson()
		       .contentLocation(URI.create(ui.getPath()+"."+EXT_JSON))
		       .build();
	}
	
	@GET
	@Path(YEAR_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN) int year) {
		reset(year);
		return doGetTurtle()
		       .contentLocation(URI.create(ui.getPath()+"."+EXT_TTL))
		       .build();
	}

	static protected Resource createResourceAndLabels(URI base, Model model, int year) {
		String s_yearURI = base + YEAR_ID_STEM + year;
		Resource r_year = model.createResource(s_yearURI, INTERVALS.CalendarYear);

		
		String s_label = "Calendar Year:" + year;
		model.add(r_year, SKOS.prefLabel, s_label, "en");
		model.add(r_year, RDFS.label, s_label, "en");
		model.add(r_year, RDFS.comment, "The calendar year of " + year, "en");

		return r_year;
	}
	
	static protected Resource createResource(URI base, Model model, int year) {
		Resource r_year = createResourceAndLabels(base, model, year);
		model.add(r_year, RDF.type, SCOVO.Dimension);
		EnglishCalendar cal = new EnglishCalendar(year, Calendar.JANUARY, 1, 0, 0, 0);	
		cal.setLenient(false);
		
		
		model.add(r_year, INTERVALS.hasXsdDurationDescription, oneYear);
		model.add(r_year, TIME.hasDurationDescription, INTERVALS.one_year);
		
		Resource r_instant = InstantDoc.createResource(base, model, cal);	
		model.add(r_year, TIME.hasBeginning, r_instant);

		model.add(r_year, SCOVO.min, formatScvDate(cal, iso8601dateformat, XSDDatatype.XSDdate));	
		
		cal.add(Calendar.YEAR, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, model, cal);	
		model.add(r_year, TIME.hasEnd, r_EndInstant);

		cal.add(Calendar.SECOND, -1);
		model.add(r_year, SCOVO.max, formatScvDate(cal, iso8601dateformat, XSDDatatype.XSDdate));	

		return r_year;
	}

	@Override
	protected void addContainedIntervals() {
		Resource halves[] = new Resource[2];
		Resource quarters[] = new Resource[4];
		Resource months[] = new Resource[12];
		
		
		// Add the Half Years
		for (int half = 0; half < 2; half++) {
			Resource r_half = HalfDoc.createResourceAndLabels(base, model, year, half+1);
			halves[half] = r_half;
			connectToContainingInterval(model, r_thisTemporalEntity, r_half);
		}
		RDFList r_halves = model.createList(halves);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsHalves, r_halves);
	

		// Add the Quarters
		for (int quarter = 0; quarter < 4; quarter++) {
			Resource r_quarter = QuarterDoc.createResourceAndLabels(base, model, year, quarter+1);
			connectToContainingInterval(model, r_thisTemporalEntity, r_quarter);
			quarters[quarter] = r_quarter;
		}
		Resource r_quarters = model.createList(quarters);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsQuarters, r_quarters);
		
		// Add the Months
		for (int month = 0; month < 12; month++) {
			Resource r_month = MonthDoc.createResourceAndLabels(base, model, year, month+1);		
			connectToContainingInterval(model, r_thisTemporalEntity, r_month);
			months[month] = r_month;
		}
		RDFList r_months = model.createList(months);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsMonths, r_months);
	}

	@Override
	protected void addContainingIntervals() {
		//Do nothing for Years
		
	}

	@Override
	protected void addNeighboringIntervals() {
		EnglishCalendar cal = (EnglishCalendar) startTime.clone();
		
		cal.add(Calendar.YEAR, 1);
		Resource r_nextYear = createResourceAndLabels(base, model, cal.get(Calendar.YEAR));
		cal = (EnglishCalendar) startTime.clone();
		cal.add(Calendar.YEAR, -1);
		Resource r_prevYear = createResourceAndLabels(base, model, cal.get(Calendar.YEAR));
		connectToNeigbours(model, r_thisTemporalEntity, r_nextYear, r_prevYear);
	}

	@Override
	protected void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year);
		
		addGeneralIntervalTimeLink(model, startTime.getTime(), oneYear);
	}
}