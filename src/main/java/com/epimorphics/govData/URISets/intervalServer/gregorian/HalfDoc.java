package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.util.Calendar;
import java.util.Locale;

import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;

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

@Path(URITemplate.HALF_DOC_STEM)
public class HalfDoc extends Doc {

	protected void reset(int year, int half) {
		reset();
		this.year = year;
		this.half = half;
		this.month=((half-1)*6)+1;
		this.quarter=((half-1)*2)+1;
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
	}
	
	@GET
	@Path(HALF_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(HALF_TOKEN) int half,
			@PathParam(EXT_TOKEN)  String ext ) {
		
		reset(year, half);
		this.ext  = ext;
		return doGet();
	}

	@GET
	@Path(HALF_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN) int year, 
			@PathParam(HALF_TOKEN) int half) {
		reset(year, half);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(HALF_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN) int year, 
			@PathParam(HALF_TOKEN) int half) {
		reset(year, half);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();
	}
	
	@GET
	@Path(HALF_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN) int year, 
			@PathParam(HALF_TOKEN) int half) {
		reset(year, half);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(HALF_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN) int year, 
			@PathParam(HALF_TOKEN) int half) {
		reset(year, half);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}

	static protected Resource createResourceAndLabels(URI base, Model m, int year, int half) {
		String relPart = year + HALF_PREFIX + half;
	
		String s_halfURI = base + HALF_ID_STEM + relPart;
		Resource r_half = m.createResource(s_halfURI, INTERVALS.CalendarHalf);
		
		if(half>0 && half<=4) {
			Resource r_quarterType = half == 1 ? INTERVALS.H1 : INTERVALS.H2;
			r_half.addProperty(RDF.type, r_quarterType);
		}
	
		String s_label = "Gregorian Half:" + relPart;
		m.add(r_half, SKOS.prefLabel, s_label, "en");
		m.add(r_half, RDFS.label, s_label, "en");
		m.add(r_half, RDFS.comment, "The " + ((half == 1) ? "first" : "second")
				+ " half of calendar year " + year, "en");
		return r_half;
	}

	static protected Resource createResource(URI base, Model m, int year, int half) {
		Resource r_half = createResourceAndLabels(base, m, year, half);

		//Add more rdf:type'ing
		r_half.addProperty(RDF.type, SCOVO.Dimension);
		r_half.addProperty(RDF.type, INTERVALS.Half);
		if(half>0 && half<=2 ) {
			r_half.addProperty(RDF.type, (half==1 ? INTERVALS.H1 : INTERVALS.H2 ));
		}

		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(year, (half-1)*6, 1, 0, 0, 0);
		cal.setLenient(false);
		
		m.add(r_half, INTERVALS.hasXsdDurationDescription, oneSecond);
		m.add(r_half, TIME.hasDurationDescription, INTERVALS.one_half);

		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_half, TIME.hasBeginning, r_instant);
		m.add(r_half, SCOVO.min, formatScvDate(cal, iso8601dateformat), XSDDatatype.XSDdate);

		cal.add(Calendar.MONTH, 6);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_half, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_half, SCOVO.max, formatScvDate(cal, iso8601dateformat), XSDDatatype.XSDdate);

		return r_half;
	}

	@Override
	void addContainedIntervals() {
		Resource quarters[] = new Resource[2];
		Resource months[] = new Resource[6];

		// Add the Quarters
		for (int quarter = 0; quarter < 2; quarter++) {
			int i_qoy = ((half - 1) * 2) + quarter + 1;
			Resource r_quarter = QuarterDoc.createResourceAndLabels(base, model, year, i_qoy);
			connectToContainingInterval(model, r_thisTemporalEntity, r_quarter);	
			quarters[quarter] = r_quarter;
		}
		RDFList r_quarters = model.createList(quarters);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsQuarters, r_quarters);

		// Add the Months
		for (int month = 0; month < 6; month++) {
			int i_moy = ((half - 1) * 6) + month + 1;
			Resource r_month = MonthDoc.createResourceAndLabels(base, model, year, i_moy);	
			connectToContainingInterval(model, r_thisTemporalEntity, r_month);
			months[month] = r_month;
		}
		RDFList r_months = model.createList(months);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsMonths, r_months);
	}

	@Override
	void addContainingIntervals() {
		Resource r_year = YearDoc.createResourceAndLabels(base, model, year);
		connectToContainingInterval(model, r_year, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		GregorianOnlyCalendar cal;
		Resource r_nextHalf;
		Resource r_prevHalf;

		try{
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.getTimeInMillis();
			cal.add(Calendar.MONTH,6);
			r_nextHalf = createResourceAndLabels(base, model, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)/6)+1);
			
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.MONTH,-6);
			r_prevHalf = createResourceAndLabels(base ,model, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)/6)+1);	
			
		} catch (IllegalArgumentException e){
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		connectToNeigbours(model, r_thisTemporalEntity, r_nextHalf, r_prevHalf);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, half);
		
		addGeneralIntervalTimeLink(model, startTime, oneHalf);
	}
}

